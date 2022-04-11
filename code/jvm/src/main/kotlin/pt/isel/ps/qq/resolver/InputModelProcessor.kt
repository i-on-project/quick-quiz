package pt.isel.ps.qq.resolver

class InputModelProcessor {

    fun getImpOf(input: Map<String,*>, type: Class<*>): Any {
        val fields = type.declaredFields
        val classes = fields.map { it.type }.toTypedArray()
        val constructor = type.getConstructor(*classes)
        val parameters = fields.map { input[it.name] }
        return constructor.newInstance(*parameters.toTypedArray())
    }
}