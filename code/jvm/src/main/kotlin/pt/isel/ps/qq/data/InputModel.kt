package pt.isel.ps.qq.data

data class Field(
    val name: String,
    val value: Any?
)

data class InputModel( val fields: List<Field> ) {
    fun getFieldsMap(): Map<String, Any?> = fields.associate { it.name to it.value }
}