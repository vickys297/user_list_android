package com.example.randomuserlisting.dataSource

import androidx.paging.*
import androidx.paging.LoadType.*
import androidx.room.withTransaction
import com.example.randomuserlisting.localDatabase.AppDatabase
import com.example.randomuserlisting.model.RemoteKeyUserModel
import com.example.randomuserlisting.model.UserModel
import com.example.randomuserlisting.network.RandomUserService
import com.example.randomuserlisting.network.RetrofitServices
import com.example.randomuserlisting.utils.AppConstants
import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException

const val STARTING_PAGE_INDEX = 1

@ExperimentalPagingApi
internal val TAG = UserDataSourceMediator::class.java.canonicalName


@ExperimentalPagingApi
class UserDataSourceMediator(
    private val database: AppDatabase,
    private val retrofitServices: RetrofitServices
) : RemoteMediator<Int, UserModel>() {


    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, UserModel>
    ): MediatorResult {
        val page = when (val pageKeyData = getKeyPageData(loadType, state)) {
            is MediatorResult.Success -> {
                return pageKeyData
            }
            else -> pageKeyData as Int
        }

        try {
            val apiService = retrofitServices.createUserListService(RandomUserService::class.java)
            val response = apiService.getUserList(AppConstants.Network.DEFAULT_LOAD_SIZE)

            database.withTransaction {
                val data = response.body()
                data?.let { _it ->
                    val empty = _it.results.isEmpty()
                    if (loadType == REFRESH) {
                        database.remoteUserDao().deleteAll()
                        database.userDao().deleteAll()
                    }
                    val previousKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                    val nextKey = if (empty) null else page + 1

                    val keysMap = data.results.map {
                        RemoteKeyUserModel(
                            phone = it.phone,
                            previousKey = previousKey,
                            nextKey = nextKey
                        )
                    }
                    database.userDao().insertAll(data.results)
                    database.remoteUserDao().insertAll(keysMap)
                }

            }

            return MediatorResult.Success(endOfPaginationReached = false)

        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getKeyPageData(
        loadType: LoadType,
        state: PagingState<Int, UserModel>
    ): Any {
        when (loadType) {
            REFRESH -> {
                val key = getCurrentPPositionKey(state)
                return key?.nextKey?.minus(1) ?: STARTING_PAGE_INDEX
            }
            PREPEND -> {
                val firstPositionKey = getFirstPositionKey(state)
                return firstPositionKey?.previousKey ?: MediatorResult.Success(
                    endOfPaginationReached = false
                )
            }
            APPEND -> {
                val lastPositionKey = getLastPositionKey(state)
                return lastPositionKey?.nextKey ?: MediatorResult.Success(
                    endOfPaginationReached = false
                )
            }
        }
    }

    private suspend fun getFirstPositionKey(state: PagingState<Int, UserModel>): RemoteKeyUserModel? {
        return state.pages
            .firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { it -> database.remoteUserDao().getKeyById(it.phone) }
    }

    private suspend fun getLastPositionKey(state: PagingState<Int, UserModel>): RemoteKeyUserModel? {
        return state.pages
            .lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { it -> database.remoteUserDao().getKeyById(it.phone) }
    }

    private suspend fun getCurrentPPositionKey(state: PagingState<Int, UserModel>): RemoteKeyUserModel? {
        return state.anchorPosition?.let { it ->
            state.closestItemToPosition(it)?.phone?.let { _id ->
                database.remoteUserDao().getKeyById(_id)
            }
        }
    }


}