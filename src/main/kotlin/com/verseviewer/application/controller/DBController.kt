package com.verseviewer.application.controller

import com.verseviewer.application.model.*
import com.verseviewer.application.model.Snapshot
import com.verseviewer.application.model.datastructure.Range
import com.verseviewer.application.model.db.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.*
import java.sql.Connection

class DBController : Controller() {

    private

    object DbSettings {
        var dbPath = ""
        val db by lazy {
            Database.connect("jdbc:sqlite:file:$dbPath", "org.sqlite.JDBC")
        }
    }

    fun initDB(path : String) {
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        DbSettings.dbPath = path
    }

    fun getBooksByTranslation(translation:String) : List<Book> = transaction (DbSettings.db) {
        addLogger(StdOutSqlLogger)

        val lang = TranslationDAO.find { Translations.name eq translation }.first().lang
        val books = object : BooksTable(lang) {}

        books.selectAll().map {
            Book(book_id = it[books.bookId],
                    name = it[books.name],
                    type = it[books.type])
        }
    }

    fun getBookVerses(translation: String, book: Int) : List<Passage> = transaction(DbSettings.db) {
        addLogger(StdOutSqlLogger)

        val trans = TranslationDAO.find { Translations.name eq translation }.first()
        val table = object : TranslationBible(translation) {}
        val books = object : BooksTable(trans.lang) {}

        table.join(books, JoinType.INNER, additionalConstraint = {table.bookId eq books.bookId})
                .select { table.bookId eq book }
                .orderBy(table.verseId to SortOrder.ASC)
                .map { Passage(id = it[table.verseId],
                              translation = Translation(trans.id.value, trans.abbreviation, trans.lang, trans.isDeutercanonic),
                              book = it[books.name],
                              chapter = it[table.chapter],
                              verse = toRange(it[table.verse]),
                              text = it[table.text])
                }
    }

    fun exchangeVersesByTranslation(passages: List<Passage>, translation: String) : List<Passage> = transaction(DbSettings.db) {
        addLogger(StdOutSqlLogger)

        val trans = TranslationDAO.find { Translations.name eq translation }.first()
        val ids = passages.map { it.id }
        val table = object : TranslationBible(translation) {}
        val books = object : BooksTable(trans.lang) {}

        table.join(books, JoinType.INNER, additionalConstraint = {table.bookId eq books.bookId})
                .select { table.verseId inList ids }
                .orderBy(table.verseId to SortOrder.ASC)
                .map { Passage(id = it[table.verseId],
                            translation = Translation(trans.id.value, trans.abbreviation, trans.lang, trans.isDeutercanonic),
                            book = it[books.name],
                            chapter = it[table.chapter],
                            verse = toRange(it[table.verse]),
                            text = it[table.text])
                }
    }

    fun getTranslations() : List<Translation> = transaction(DbSettings.db) {
        addLogger(StdOutSqlLogger)

        TranslationDAO.all().map { Translation(it) }
    }

    fun updateVerseText(passage: Passage) {
        transaction(DbSettings.db) {
            addLogger(StdOutSqlLogger)

            val table = object : TranslationBible(passage.translation.name) {}
            table.update ({ table.verseId eq passage.id} ) {
                it[table.text] = passage.text
            }
        }
    }

    fun getSnapshot(id : Int) = transaction(DbSettings.db) {
        addLogger(StdOutSqlLogger)
        print("DD")
        SnapshotTable
            .select { SnapshotTable.id eq id }
            .map {
                Snapshot(it[SnapshotTable.id].value,
                    it[SnapshotTable.layout],
                    it[SnapshotTable.display],
                    it[SnapshotTable.orientation],
                    it[SnapshotTable.textAlignment],
                    it[SnapshotTable.fontSize],
                    it[SnapshotTable.fontFamily],
                    it[SnapshotTable.fontPosture],
                    it[SnapshotTable.fontWeight],
                    it[SnapshotTable.textFill],
                    it[SnapshotTable.textStroke],
                    it[SnapshotTable.textStrokeWidth])
        }
    }.first()

    fun updateUserPreference(snapshot : Snapshot) {
        transaction(DbSettings.db) {
            addLogger(StdOutSqlLogger)

            SnapshotTable.update({SnapshotTable.id eq snapshot.id}) {
                it[display] = snapshot.displayIndexProperty.value
                it[orientation] = snapshot.orientationProperty.value.toString()
                it[textAlignment] = snapshot.textAlignmentProperty.value.toString()
                it[fontSize] = snapshot.fontSizeProperty.value.toDouble()
                it[fontFamily] = snapshot.fontFamilyProperty.value
                it[fontWeight] = snapshot.fontWeightProperty.value.toString()
                it[fontPosture] = snapshot.fontPostureProperty.value.toString()
                it[textFill] = snapshot.fillProperty.value.toString()
                it[textStroke] = snapshot.strokeProperty.value.toString()
                it[textStrokeWidth] = snapshot.strokeWidthProperty.value
            }
        }
    }

    fun updateLayout(snapshot : Snapshot) {
        transaction(DbSettings.db) {
            addLogger(StdOutSqlLogger)

            SnapshotTable.update ( {SnapshotTable.id eq snapshot.id} ) {
                it[layout] = snapshot.layoutToString()
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

