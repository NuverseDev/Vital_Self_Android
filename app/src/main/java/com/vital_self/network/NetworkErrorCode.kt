package com.vital_self.network

object NetworkErrorCode {
    fun getNetworkError(code:Int, message:String?):String{
        when(code){
            100 -> {
                return ("No Internet Available.")
            }
            400 -> {
                return ("Bad Request: please try again")
            }
            401 -> {
                return ("User not Found or registered please contact your provider")
            }
            403 -> {
                return ("Forbidden : You do not have access")
            }
            404 -> {
                return ("User not Found or registered please contact your provider")
            }
            408 -> {
                return ("Request Timeout: Please try again later")
            }

            500 -> {
                return ("Internal Server Error")
            }
            502 -> {
                return ("Bad Gateway: Please try again later")
            }
            504 -> {
                return ("Gateway Timeout: Please try again later")
            }
            else -> {
                return if (message.isNullOrEmpty()) "Something went wrong" else message
            }
        }
    }
}