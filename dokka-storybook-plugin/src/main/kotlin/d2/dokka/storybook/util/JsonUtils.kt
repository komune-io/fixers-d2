package d2.dokka.storybook.util

import tools.jackson.core.json.JsonReadFeature
import tools.jackson.databind.json.JsonMapper

object JsonUtils {
    private val mapper = JsonMapper.builder()
        .enable(JsonReadFeature.ALLOW_UNQUOTED_PROPERTY_NAMES)
        .build()

    fun toPrettyJson(json: String): String {
        val parsed = mapper.readValue(json, Any::class.java)
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(parsed)
    }
}
