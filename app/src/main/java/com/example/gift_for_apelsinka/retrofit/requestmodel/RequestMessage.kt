package com.example.gift_for_apelsinka.retrofit.requestmodel

data class RequestMessage(val ip: String,
                          val who: Int,
                          val toWhom: Int,
                          val text: String)
