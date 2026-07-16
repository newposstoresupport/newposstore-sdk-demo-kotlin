package com.android.newpos.store.sdk.demo.base

data class LoadingOption(
    var loading: Boolean = false,
    var loadingText: String = ""
) {
    constructor(loading: Boolean) : this(loading, "")
    constructor(loadingText: String) : this(true, loadingText)
}
