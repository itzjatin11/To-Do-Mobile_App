package com.example.todolist.domain

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream

object RssParser {
    fun parse(inputStream: InputStream): List<RssItem> {
        val items = mutableListOf<RssItem>()
        val parser: XmlPullParser = Xml.newPullParser()
        parser.setInput(inputStream, null)

        var eventType = parser.eventType
        var title: String? = null
        var link: String? = null
        var pubDate: String? = null
        var description: String? = null

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        "item" -> {
                            title = null
                            link = null
                            pubDate = null
                            description = null
                        }
                        "title" -> title = parser.nextText()
                        "link" -> link = parser.nextText()
                        "pubDate" -> pubDate = parser.nextText()
                        "description" -> description = parser.nextText()
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (parser.name == "item") {
                        items.add(RssItem(title, link, pubDate, description))
                    }
                }
            }
            eventType = parser.next()
        }
        inputStream.close()
        return items
    }
}