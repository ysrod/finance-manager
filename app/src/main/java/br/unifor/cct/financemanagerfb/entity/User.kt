package br.unifor.cct.financemanagerfb.entity

data class User(

    val id:String = "",
    val email : String ="",
    val name : String = "",
    val phone : String = "",
    val finances : HashMap<String, Finances> = hashMapOf()

)
