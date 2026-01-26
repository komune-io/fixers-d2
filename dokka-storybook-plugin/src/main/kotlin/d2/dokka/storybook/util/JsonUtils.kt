package d2.dokka.storybook.util

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper

object JsonUtils {
    private val mapper = ObjectMapper()
        .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)

    fun toPrettyJson(json: String): String {
        val parsed = mapper.readValue(json, Any::class.java)
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(parsed)
    }
}
