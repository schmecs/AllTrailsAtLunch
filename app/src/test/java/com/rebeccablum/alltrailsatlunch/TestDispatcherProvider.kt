package com.rebeccablum.alltrailsatlunch

import com.rebeccablum.alltrailsatlunch.util.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
class TestDispatcherProvider : DispatcherProvider {
    override fun default(): CoroutineDispatcher = TestCoroutineDispatcher()
    override fun io(): CoroutineDispatcher = TestCoroutineDispatcher()
    override fun main(): CoroutineDispatcher = TestCoroutineDispatcher()
    override fun unconfined(): CoroutineDispatcher = TestCoroutineDispatcher()
}