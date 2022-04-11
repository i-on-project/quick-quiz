package pt.isel.ps.qq.data

data class Field(
    val name: String,
    val value: Any,
)

data class InputModel(
    val fields: List<Field>
)

