package com.verseviewer.application.controller

import com.verseviewer.application.model.*
import com.verseviewer.application.model.datastructure.Range
import com.verseviewer.application.model.db.*
import com.verseviewer.application.model.event.SendDBNotification
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.*
import java.sql.Connection

class DBController : Controller() {


    private val dbname = "data/bible.db"
    private val jdbc = "jdbc:sqlite:file:"
    private val driver = "org.sqlite.JDBC"

    var db : Database? = null

    fun isConnected() = db != null

    fun connectToDB(path : String) {
        db = Database.connect(jdbc + path, driver)
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    }

    fun getBooksByTranslation(translation:String) : List<Book> = transaction (db) {
        addLogger(StdOutSqlLogger)
        val lang = TranslationDAO.find { Translations.name eq translation }.first().lang
        val books = object : BooksTable(lang) {}
        books.selectAll().map {
            Book(book_id = it[books.bookId],
                    name = it[books.name],
                    type = it[books.type])
        }
    }

    fun getBookVerses(translation: String, book: Int) : List<Passage> = transaction(db) {
        addLogger(StdOutSqlLogger)
        val trans = TranslationDAO.find { Translations.name eq translation }.first()
        val table = object : TranslationBible(translation) {}
        val books = object : BooksTable(trans.lang) {}
        table.join(books, JoinType.INNER, additionalConstraint = {table.bookId eq books.bookId})
                .select { table.bookId eq book }
                .map { Passage(id = it[table.verseId],
                              translation = Translation(trans.id.value, trans.abbreviation, trans.lang, trans.isDeutercanonic),
                              book = it[books.name],
                              chapter = it[table.chapter],
                              verse = toRange(it[table.verse]),
                              text = it[table.text])
                }
    }

    fun exchangeVersesByTranslation(passages: List<Passage>, translation: String) : List<Passage> = transaction(db) {
        addLogger(StdOutSqlLogger)
        val trans = TranslationDAO.find { Translations.name eq translation }.first()
        val ids = passages.map { it.id }
        val table = object : TranslationBible(translation) {}
        val books = object : BooksTable(trans.lang) {}
        table.join(books, JoinType.INNER, additionalConstraint = {table.bookId eq books.bookId})
                .select { table.verseId inList ids }
                .map { Passage(id = it[table.verseId],
                            translation = Translation(trans.id.value, trans.abbreviation, trans.lang, trans.isDeutercanonic),
                            book = it[books.name],
                            chapter = it[table.chapter],
                            verse = toRange(it[table.verse]),
                            text = it[table.text])
                }
    }

    fun getTranslations() : List<Translation> = transaction(db) {
        addLogger(StdOutSqlLogger)
        TranslationDAO.all().map { Translation(it) }
    }

    fun updateVerseText(passage: Passage) {
        transaction(db) {
            addLogger(StdOutSqlLogger)
            val table = object : TranslationBible(passage.translation.name) {}
            table.update ({ table.verseId eq passage.id} ) {
                it[table.text] = passage.text
            }
        }
    }

    fun getUsers() : List<User> = transaction(db) {
        addLogger(StdOutSqlLogger)
        try {
            Users.slice(Users.id, Users.name, Users.layout).selectAll().map {
                User(it[Users.id].value,
                        "${it[Users.name]}",
                        it[Users.layout])
            }
        } catch (e : ExposedSQLException) {
            fire(SendDBNotification(e.localizedMessage))
            listOf()
        }
    }

    fun addUser(user : User, userPref : Preference) {
        transaction(db) {
            addLogger(StdOutSqlLogger)
            val id = Users.insertAndGetId {
                it[name] = user.name
                it[layout] = user.layoutToString()
                it[display] = userPref.displayIndexProperty.value
                it[orientation] = userPref.orientationProperty.value.toString()
                it[textAlignment] = userPref.textAlignmentProperty.value.toString()
                it[fontSize] = userPref.fontSizeProperty.value.toDouble()
                it[fontFamily] = userPref.fontFamilyProperty.value
                it[fontWeight] = userPref.fontWeightProperty.value.toString()
                it[fontPosture] = userPref.fontPostureProperty.value.toString()
                it[textFill] = userPref.fillProperty.value.toString()
                it[textStroke] = userPref.strokeProperty.value.toString()
                it[textStrokeWidth] = userPref.strokeWidthProperty.value
            }
            user.id = id.value
        }
    }

    fun removeUser(user : User) {
        transaction(db) {
            addLogger(StdOutSqlLogger)
            Users.deleteWhere { Users.id eq user.id }
        }
    }

    fun getPreference(user : User) = transaction(db) {
        Users.select { Users.id eq user.id }.map {
            Preference(it[Users.display],
                    it[Users.orientation],
                    it[Users.textAlignment],
                    it[Users.fontSize],
                    it[Users.fontFamily],
                    it[Users.fontPosture],
                    it[Users.fontWeight],
                    it[Users.textFill],
                    it[Users.textStroke],
                    it[Users.textStrokeWidth])
        }
    }.first()

    fun updateUserPreference(pref : Preference) {
        transaction(db) {
            addLogger(StdOutSqlLogger)
            Users.update {
                it[display] = pref.displayIndexProperty.value
                it[orientation] = pref.orientationProperty.value.toString()
                it[textAlignment] = pref.textAlignmentProperty.value.toString()
                it[fontSize] = pref.fontSizeProperty.value.toDouble()
                it[fontFamily] = pref.fontFamilyProperty.value
                it[fontWeight] = pref.fontWeightProperty.value.toString()
                it[fontPosture] = pref.fontPostureProperty.value.toString()
                it[textFill] = pref.fillProperty.value.toString()
                it[textStroke] = pref.strokeProperty.value.toString()
                it[textStrokeWidth] = pref.strokeWidthProperty.value
            }
        }
    }

    fun updateLayout(user : User) {
        transaction(db) {
            addLogger(StdOutSqlLogger)
            Users.update ( {Users.id eq user.id} ) {
                it[layout] = user.layoutToString()
            }
        }
    }


    private fun toRange(verse : String) : Range {
        val trim = verse.replace("\\s".toRegex(), "")
        return when {
            trim.contains("-") -> {
               val split = trim.split("-")
               Range(split.first().toInt(), split.last().toInt())
            }
            else -> {
                Range(trim.toInt(), trim.toInt())
            }
        }
    }

}

