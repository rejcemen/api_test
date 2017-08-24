package ru.test

import org.json4s.JsonAST.{JArray, JString, JValue}

object JSONUtil {
    def sectionsWithId(json:JValue, id:String): List[JValue] = {
        for (
            providerFieldsSections <- (json \\ "providerFields").children;
            sectionWithId <- providerFieldsSections;
            if (sectionWithId \ "id") == JString(id)
        ) yield sectionWithId
    }
}
