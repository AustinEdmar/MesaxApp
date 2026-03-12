package com.austin.mesax.data.repository

import com.austin.mesax.data.api.TablesApi
import com.austin.mesax.data.local.dao.TableDao
import com.austin.mesax.data.local.entity.TableEntity
import com.austin.mesax.data.local.mapper.toEntity
import com.austin.mesax.data.model.TableDto
import com.austin.mesax.data.responses.AuthResponses.UserDto

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


class TablesRepository @Inject constructor(
    private val api: TablesApi,
    private val dao: TableDao
){

    fun observeTables(): Flow<List<TableEntity>> =
        dao.observeTables()

    suspend fun syncTables() {

        val remoteTables = api.getTables().data
        val localTables = dao.getTablesOnce()

        val remoteIds = remoteTables.map { it.id }
        val localIds = localTables.map { it.id }

        val toDelete = localIds - remoteIds.toSet()

        dao.deleteTablesByIds(toDelete)
        dao.insertTables(remoteTables.map { it.toEntity() })
    }







}
