package br.unifor.cct.financemanagerfb.entity

data class Finances(

    val id:String = "",
    val description : String ="",
    val amount : String ="",
    val date : String = "",
    val type : Boolean = false
//false = expense
//true = revenue

)
