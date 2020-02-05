package com.example.demo.controller

import com.example.demo.model.Book
import com.example.demo.model.TableVersesModel
import com.example.demo.model.Translation
import com.example.demo.model.Verse
import com.example.demo.model.datastructure.Range
import com.example.demo.model.db.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.*
import java.sql.Connection

class DBController : Controller() {

    private val tableModel : TableVersesModel by inject()

    companion object DBSettings {
        private const val dbname = "data/bible.db"
        private const val jdbc = "jdbc:sqlite:file:"
        private const val driver = "org.sqlite.JDBC"

        val db by lazy {
            val connection = Database.connect(jdbc + dbname, driver)
            TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
            connection
        }
    }

    fun getBooksByTranslation(translation:String) : List<Book> = transaction (db) {
        addLogger(StdOutSqlLogger)
        val lang = TranslationEntity.find { Translations.name eq translation }.first().lang
        val books = object : BooksTable(lang) {}
        books.selectAll().map {
            Book(book_id = it[books.bookId],
                    name = it[books.name],
                    type = it[books.type])
        }
    }

    fun getBookVerses(translation: String, book: Int) : List<Verse> = transaction(db) {
        addLogger(StdOutSqlLogger)

        val trans = TranslationEntity.find { Translations.name eq translation }.first()
        val table = object : TranslationBible(translation) {}
        val books = object : BooksTable(trans.lang) {}
        table.join(books, JoinType.INNER, additionalConstraint = {table.bookId eq books.bookId})
                .select { table.bookId eq book }
                .map { Verse(id = it[table.verseId],
                              translation = Translation(trans.id.value, trans.abbreviation, trans.lang, trans.isDeutercanonic),
                              book = it[books.name],
                              chapter = it[table.chapter],
                              verse = toRange(it[table.verse]),
                              text = it[table.text])
                }
    }

    fun exchangeVersesByTranslation(verses: List<Verse>, translation: String) : List<Verse> = transaction(db) {
        addLogger(StdOutSqlLogger)
        val trans = TranslationEntity.find { Translations.name eq translation }.first()
        val ids = verses.map { it.id }
        val table = object : TranslationBible(translation) {}
        val books = object : BooksTable(trans.lang) {}
        table.join(books, JoinType.INNER, additionalConstraint = {table.bookId eq books.bookId})
                .select { table.verseId inList ids }
                .map { Verse(id = it[table.verseId],
                            translation = Translation(trans.id.value, trans.abbreviation, trans.lang, trans.isDeutercanonic),
                            book = it[books.name],
                            chapter = it[table.chapter],
                            verse = toRange(it[table.verse]),
                            text = it[table.text])
                }
    }

    fun getTranslations() : List<Translation> = transaction(db) {
        addLogger(StdOutSqlLogger)
        TranslationEntity.all().map {
            Translation(name = it.id.value,
                        abbreviation = it.abbreviation,
                        lang = it.lang,
                        isDeutercanonic = it.isDeutercanonic)
        }
    }

    fun updateVerseText(verse: Verse) {
        transaction(db) {
            addLogger(StdOutSqlLogger)
            val table = object : TranslationBible(verse.translation.name) {}
            table.update ({ table.verseId eq verse.id} ) {
                it[table.text] = verse.text
            }
        }
    }

    private fun toRange(verse : String) : Range {
        val trim = verse.replace("\\s".toRegex(), "")
        return when {
            trim.contains("-") -> {
               val split = trim.split("-")
               Range(split.first().toInt().rangeTo(split.last().toInt()))
            }
            else -> {
                Range(trim.toInt().rangeTo(trim.toInt()))
            }
        }
    }

}

