//package com.auto.artist.ui
//
//import androidx.lifecycle.ViewModel
//import com.auto.artist.db.ImageEntity
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//
//class SharedViewModel(
//) : ViewModel() {
//
//
//    private var _selectedImage: MutableStateFlow<ImageEntity?> = MutableStateFlow(null)
//    val selectedImage = _selectedImage.asStateFlow()
//
//
//    fun updateSelectedImage(image: ImageEntity?) {
//        _selectedImage.value = image
//    }
//
//
//}