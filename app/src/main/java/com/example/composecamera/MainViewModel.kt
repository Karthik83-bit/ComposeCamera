package com.example.composecamera

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    val permissionVisibleDialog= mutableStateListOf<String>()
  fun removeFromQueue(){
     permissionVisibleDialog.removeLast()
  }
    fun addPermission(
        isGranted:Boolean,
        permission:String
    ){
        if(!isGranted){
            permissionVisibleDialog.add(permission)
        }
    }
}
