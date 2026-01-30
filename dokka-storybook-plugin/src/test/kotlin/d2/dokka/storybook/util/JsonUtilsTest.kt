package d2.dokka.storybook.util

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class JsonUtilsTest {

    @Test
    fun `toPrettyJson formats simple object`() {
        val input = """{"name":"John","age":30}"""
        val result = JsonUtils.toPrettyJson(input)

        val expected = """
            {
              "name" : "John",
              "age" : 30
            }
        """.trimIndent()
        assertEquals(expected, result)
    }

    @Test
    fun `toPrettyJson formats nested object`() {
        val input = """{"person":{"name":"John","address":{"city":"Paris"}}}"""
        val result = JsonUtils.toPrettyJson(input)

        val expected = """
            {
              "person" : {
                "name" : "John",
                "address" : {
                  "city" : "Paris"
                }
              }
            }
        """.trimIndent()
        assertEquals(expected, result)
    }

    @Test
    fun `toPrettyJson formats array`() {
        val input = """{"items":[1,2,3]}"""
        val result = JsonUtils.toPrettyJson(input)

        val expected = """
            {
              "items" : [ 1, 2, 3 ]
            }
        """.trimIndent()
        assertEquals(expected, result)
    }

    @Test
    fun `toPrettyJson handles unquoted field names`() {
        val input = """{name:"John",age:30}"""
        val result = JsonUtils.toPrettyJson(input)

        val expected = """
            {
              "name" : "John",
              "age" : 30
            }
        """.trimIndent()
        assertEquals(expected, result)
    }

    @Test
    fun `toPrettyJson handles empty object`() {
        val input = """{}"""
        val result = JsonUtils.toPrettyJson(input)

        assertEquals("{ }", result)
    }

    @Test
    fun `toPrettyJson throws on invalid json`() {
        val input = """not valid json"""

        assertFailsWith<Exception> {
            JsonUtils.toPrettyJson(input)
        }
    }
}
