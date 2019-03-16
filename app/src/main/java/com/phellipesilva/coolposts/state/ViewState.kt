package com.phellipesilva.coolposts.state

import com.phellipesilva.coolposts.R

enum class ViewState(val msgStringId: Int, val isLoading: Boolean) {
    SUCCESS(R.string.app_updated_msg, false),
    UNEXPECTED_ERROR(R.string.unexpected_error_msg, false),
    NO_INTERNET(R.string.no_connection_msg, false)
}