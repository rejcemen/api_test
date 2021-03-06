package ru.test

import java.util

import org.json4s.DefaultFormats

import scala.collection.JavaConverters._
import org.json4s.JsonAST.{JArray, JObject, JString}
import org.scalatest.junit.JUnitSuite
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.junit.{Before, Test}

@RunWith(classOf[Parameterized])
class APITest(groupName: String) extends JUnitSuite {

    final val BaseUrl: String = "https://www.tinkoff.ru/api/v1/providers?groups="
    val testConnection = new TestConnection()

    @Before
    def setUp() {
        testConnection.responseContent.toSome match {
            case Some(c) => // do nothing - already initialized
            case None => testConnection.get(BaseUrl + groupName)
        }
    }

    /**
        Verifies that HTTP status code for request belongs to success codes range
        Priority: 1
      */
    @Test
    def httpStatusCodeSuccess() {
        assert(200 until 300 contains testConnection.responseCode)
    }

    /**
        Verifies that received document represents JSON formatted string
        Priority: 2
      */
    @Test
    def documentIsJSON() {
        assert(testConnection.responseContent.isInstanceOf[JObject])
    }

    /**
        Verifies that resultCode attribute value is OK
        Priority: 3
      */
    @Test
    def resultCodeIsOK() {
        assert((testConnection.responseContent \\ "resultCode").toSome == Option(JString("OK")))
    }

    /**
        Verifies that every groupId value equal to given group name
        Priority: 4
      */
    @Test
    def everyGroupIdEqualToGroupName() {
        (testConnection.responseContent \\ "groupId").children.foreach(c => {
            assert(c == JString(groupName))
        })
    }

    /**
        Verifies that for each id equal to "phone" the "name" parameter contains "[Н|номер телефона]" substring
        Priority: 5
      */
    @Test
    def sectionsWithIdPhoneContainsSubstring() {
        val sectionsWithId = JSONUtil.sectionsWithId(testConnection.responseContent, "phone")
        if (sectionsWithId.isEmpty) {
            fail(s"No sections with id 'phone' for group name $groupName")
        }
        sectionsWithId.map(x => x \ "name").foreach { name =>
            implicit val formats = DefaultFormats
            assert(name.extract[String].toLowerCase.contains("номер телефона"))
        }
    }
}

object APITest {
    @Parameters
    def groupNames(): util.Collection[Array[String]] = {
        Iterable(
            Array("Переводы"),
            Array("Интернет"),
            Array("Благотворительность")
        ).asJavaCollection
    }
}
