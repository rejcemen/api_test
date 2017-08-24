package ru.test

import java.io.IOException

import org.apache.http.HttpResponse
import org.apache.http.client.ResponseHandler
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.{CloseableHttpClient, HttpClients}
import org.apache.http.util.EntityUtils
import org.json4s._
import org.json4s.jackson.JsonMethods._


class TestConnection {

    var responseContent: JValue = JNothing
    var responseCode: Int = 0

    /**
      * Sends HTTP Get request to given resource and receives status code and content
      * @param url requested URL string
      * @return true if response received, false if there were problems with receiving response
      */
    def get(url: String): Boolean = {
        val httpClient: CloseableHttpClient = HttpClients.createDefault()
        try {
            val httpGet: HttpGet = new HttpGet(url)
            val responseHandler = new ResponseHandler[(Int, String)]() {
                @throws[IOException]
                override def handleResponse(response: HttpResponse): (Int, String) = {
                    val status = response.getStatusLine.getStatusCode()
                    if (status >= 200 && status < 300) {
                        val entity = Option(response.getEntity)
                        val content = entity match {
                            case Some(e) => EntityUtils.toString(e)
                            case None => ""
                        }
                        (status, content)
                    } else {
                        (status, "")
                    }
                }
            }
            val response = httpClient.execute(httpGet, responseHandler)
            responseCode = response._1
            responseContent = parse(response._2)
            true
        } catch {
            case e: Exception => e.printStackTrace(); false
        } finally {
            httpClient.close()
        }
    }


}
