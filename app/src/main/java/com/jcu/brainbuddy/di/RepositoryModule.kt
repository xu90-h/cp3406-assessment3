package com.jcu.brainbuddy.di

import com.jcu.brainbuddy.data.repository.QuestionRepository
import com.jcu.brainbuddy.data.repository.QuestionRepositoryImpl
import com.jcu.brainbuddy.data.repository.ResultRepository
import com.jcu.brainbuddy.data.repository.ResultRepositoryImpl
import com.jcu.brainbuddy.data.repository.SettingsRepository
import com.jcu.brainbuddy.data.repository.SettingsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindQuestionRepository(impl: QuestionRepositoryImpl): QuestionRepository

    @Binds
    @Singleton
    abstract fun bindResultRepository(impl: ResultRepositoryImpl): ResultRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}
