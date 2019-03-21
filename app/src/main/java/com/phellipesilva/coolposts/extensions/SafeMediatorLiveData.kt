package com.phellipesilva.coolposts.extensions

import androidx.lifecycle.MediatorLiveData

class SafeMediatorLiveData<T : Any>(initialValue: T) : MediatorLiveData<T>() {

  init {
    value = initialValue
  }

  override fun getValue(): T = super.getValue()!!
}