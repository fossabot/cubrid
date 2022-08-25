/*
 * Copyright 2008 Search Solution Corporation
 * Copyright 2016 CUBRID Corporation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */


/*
 * network.h -Definitions for client/server network support.
 */

#ifndef _NETWORK_H_
#define _NETWORK_H_

#ident "$Id$"

#include "perf_monitor.h"
#include "locator.h"
#include "log_comm.h"

/* Server statistics structure size, used to make sure the pack/unpack
   routines follow the current structure definition.
   This must be the byte size of the structure
   as returned by sizeof().  Note that MEMORY_STAT_SIZE and PACKED_STAT_SIZE
   are not necessarily the same although they will be in most cases.
*/

#define EXECUTE_QUERY_MAX_ARGUMENT_DATA_SIZE  512

/* These define the requests that the server will respond to */
/* WARNING: should sync with net_server_request_name in network_common.cpp */
enum net_server_request
{
  NET_SERVER_REQUEST_START = 0,

  NET_SERVER_PING,

  NET_SERVER_BO_INIT_SERVER,
  NET_SERVER_BO_REGISTER_CLIENT,
  NET_SERVER_BO_UNREGISTER_CLIENT,
  NET_SERVER_BO_BACKUP,
  NET_SERVER_BO_ADD_VOLEXT,
  NET_SERVER_BO_CHECK_DBCONSISTENCY,
  NET_SERVER_BO_FIND_NPERM_VOLS,
  NET_SERVER_BO_FIND_NTEMP_VOLS,
  NET_SERVER_BO_FIND_LAST_PERM,
  NET_SERVER_BO_FIND_LAST_TEMP,
  NET_SERVER_BO_CHANGE_HA_MODE,
  NET_SERVER_BO_NOTIFY_HA_LOG_APPLIER_STATE,
  NET_SERVER_BO_COMPACT_DB,
  NET_SERVER_BO_HEAP_COMPACT,
  NET_SERVER_BO_COMPACT_DB_START,
  NET_SERVER_BO_COMPACT_DB_STOP,
  NET_SERVER_BO_GET_LOCALES_INFO,

  NET_SERVER_TM_SERVER_COMMIT,
  NET_SERVER_TM_SERVER_ABORT,
  NET_SERVER_TM_SERVER_START_TOPOP,
  NET_SERVER_TM_SERVER_END_TOPOP,
  NET_SERVER_TM_SERVER_SAVEPOINT,
  NET_SERVER_TM_SERVER_PARTIAL_ABORT,
  NET_SERVER_TM_SERVER_HAS_UPDATED,
  NET_SERVER_TM_SERVER_ISACTIVE_AND_HAS_UPDATED,
  NET_SERVER_TM_ISBLOCKED,
  NET_SERVER_TM_WAIT_SERVER_ACTIVE_TRANS,
  NET_SERVER_TM_SERVER_GET_GTRINFO,
  NET_SERVER_TM_SERVER_SET_GTRINFO,
  NET_SERVER_TM_SERVER_2PC_START,
  NET_SERVER_TM_SERVER_2PC_PREPARE,
  NET_SERVER_TM_SERVER_2PC_RECOVERY_PREPARED,
  NET_SERVER_TM_SERVER_2PC_ATTACH_GT,
  NET_SERVER_TM_SERVER_2PC_PREPARE_GT,
  NET_SERVER_TM_LOCAL_TRANSACTION_ID,

  NET_SERVER_LC_FETCH,
  NET_SERVER_LC_FETCHALL,
  NET_SERVER_LC_FETCH_LOCKSET,
  NET_SERVER_LC_FETCH_ALLREFS_LOCKSET,
  NET_SERVER_LC_GET_CLASS,
  NET_SERVER_LC_FIND_CLASSOID,
  NET_SERVER_LC_DOESEXIST,
  NET_SERVER_LC_FORCE,
  NET_SERVER_LC_RESERVE_CLASSNAME,
  NET_SERVER_LC_RESERVE_CLASSNAME_GET_OID,
  NET_SERVER_LC_DELETE_CLASSNAME,
  NET_SERVER_LC_RENAME_CLASSNAME,
  NET_SERVER_LC_ASSIGN_OID,
  NET_SERVER_LC_NOTIFY_ISOLATION_INCONS,
  NET_SERVER_LC_FIND_LOCKHINT_CLASSOIDS,
  NET_SERVER_LC_FETCH_LOCKHINT_CLASSES,
  NET_SERVER_LC_ASSIGN_OID_BATCH,
  NET_SERVER_LC_CHECK_FK_VALIDITY,
  NET_SERVER_LC_REM_CLASS_FROM_INDEX,
  NET_SERVER_LC_UPGRADE_INSTANCES_DOMAIN,
  NET_SERVER_LC_REPL_FORCE,
  NET_SERVER_LC_REDISTRIBUTE_PARTITION_DATA,

  NET_SERVER_HEAP_CREATE,
  NET_SERVER_HEAP_DESTROY,
  NET_SERVER_HEAP_DESTROY_WHEN_NEW,
  NET_SERVER_HEAP_GET_CLASS_NOBJS_AND_NPAGES,
  NET_SERVER_HEAP_HAS_INSTANCE,
  NET_SERVER_HEAP_RECLAIM_ADDRESSES,

  NET_SERVER_FILE_APPLY_TDE_TO_CLASS_FILES,

  NET_SERVER_DBLINK_GET_CRYPT_KEY,

  NET_SERVER_TDE_GET_DATA_KEYS,
  NET_SERVER_TDE_IS_LOADED,
  NET_SERVER_TDE_GET_MK_FILE_PATH,
  NET_SERVER_TDE_GET_MK_INFO,
  NET_SERVER_TDE_CHANGE_MK_ON_SERVER,

  NET_SERVER_LOG_RESET_WAIT_MSECS,
  NET_SERVER_LOG_RESET_ISOLATION,
  NET_SERVER_LOG_SET_INTERRUPT,
  NET_SERVER_LOG_DUMP_STAT,
  NET_SERVER_LOG_GETPACK_TRANTB,
  NET_SERVER_LOG_DUMP_TRANTB,
  NET_SERVER_LOG_FIND_LOB_LOCATOR,
  NET_SERVER_LOG_ADD_LOB_LOCATOR,
  NET_SERVER_LOG_CHANGE_STATE_OF_LOCATOR,
  NET_SERVER_LOG_DROP_LOB_LOCATOR,
  NET_SERVER_LOG_CHECKPOINT,

  NET_SERVER_LK_DUMP,

  NET_SERVER_BTREE_ADDINDEX,
  NET_SERVER_BTREE_DELINDEX,
  NET_SERVER_BTREE_LOADINDEX,
  NET_SERVER_BTREE_FIND_UNIQUE,
  NET_SERVER_BTREE_CLASS_UNIQUE_TEST,
  NET_SERVER_BTREE_GET_STATISTICS,
  NET_SERVER_BTREE_GET_KEY_TYPE,
  NET_SERVER_BTREE_FIND_MULTI_UNIQUES,

  NET_SERVER_DISK_TOTALPGS,
  NET_SERVER_DISK_FREEPGS,
  NET_SERVER_DISK_REMARKS,
  NET_SERVER_DISK_VLABEL,

  NET_SERVER_QST_GET_STATISTICS,
  NET_SERVER_QST_UPDATE_STATISTICS,
  NET_SERVER_QST_UPDATE_ALL_STATISTICS,

  NET_SERVER_QM_QUERY_PREPARE,
  NET_SERVER_QM_QUERY_EXECUTE,
  NET_SERVER_QM_QUERY_PREPARE_AND_EXECUTE,
  NET_SERVER_QM_QUERY_END,
  NET_SERVER_QM_QUERY_DROP_ALL_PLANS,
  NET_SERVER_QM_QUERY_DUMP_PLANS,
  NET_SERVER_QM_QUERY_DUMP_CACHE,

  NET_SERVER_LS_GET_LIST_FILE_PAGE,

  NET_SERVER_MNT_SERVER_START_STATS,
  NET_SERVER_MNT_SERVER_STOP_STATS,
  NET_SERVER_MNT_SERVER_COPY_STATS,
  NET_SERVER_MNT_SERVER_COPY_GLOBAL_STATS,

  NET_SERVER_CT_CHECK_REP_DIR,

  NET_SERVER_CSS_KILL_TRANSACTION,
  NET_SERVER_CSS_DUMP_CS_STAT,
  NET_SERVER_CSS_KILL_OR_INTERRUPT_TRANSACTION,

  NET_SERVER_QPROC_GET_SYS_TIMESTAMP,
  NET_SERVER_QPROC_GET_CURRENT_VALUE,
  NET_SERVER_QPROC_GET_NEXT_VALUE,
  NET_SERVER_QPROC_GET_SERVER_INFO,
  NET_SERVER_SERIAL_DECACHE,

  NET_SERVER_SYNONYM_REMOVE_XASL_BY_OID,

  NET_SERVER_JSP_GET_SERVER_PORT,

  NET_SERVER_PRM_SET_PARAMETERS,
  NET_SERVER_PRM_GET_PARAMETERS,
  NET_SERVER_PRM_GET_FORCE_PARAMETERS,
  NET_SERVER_PRM_DUMP_PARAMETERS,

  NET_SERVER_REPL_INFO,
  NET_SERVER_REPL_LOG_GET_APPEND_LSA,
  NET_SERVER_LOG_SET_SUPPRESS_REPL_ON_TRANSACTION,
  NET_SERVER_CHKSUM_REPL,

  NET_SERVER_LOGWR_GET_LOG_PAGES,

  NET_SERVER_SHUTDOWN,

  /* External storage supports */
  NET_SERVER_ES_CREATE_FILE,
  NET_SERVER_ES_WRITE_FILE,
  NET_SERVER_ES_READ_FILE,
  NET_SERVER_ES_DELETE_FILE,
  NET_SERVER_ES_COPY_FILE,
  NET_SERVER_ES_RENAME_FILE,
  NET_SERVER_ES_GET_FILE_SIZE,

  /* Session state requests */
  NET_SERVER_SES_CHECK_SESSION,
  NET_SERVER_SES_END_SESSION,
  NET_SERVER_SES_SET_ROW_COUNT,
  NET_SERVER_SES_GET_ROW_COUNT,
  NET_SERVER_SES_GET_LAST_INSERT_ID,
  NET_SERVER_SES_RESET_CUR_INSERT_ID,
  NET_SERVER_SES_CREATE_PREPARED_STATEMENT,
  NET_SERVER_SES_GET_PREPARED_STATEMENT,
  NET_SERVER_SES_DELETE_PREPARED_STATEMENT,
  NET_SERVER_SES_SET_SESSION_VARIABLES,
  NET_SERVER_SES_GET_SESSION_VARIABLE,
  NET_SERVER_SES_DROP_SESSION_VARIABLES,

  NET_SERVER_ACL_DUMP,
  NET_SERVER_ACL_RELOAD,

  NET_SERVER_AU_LOGIN_USER,
  NET_SERVER_AU_DOES_ACTIVE_USER_EXIST,

  NET_SERVER_VACUUM,
  NET_SERVER_GET_MVCC_SNAPSHOT,
  NET_SERVER_LOCK_RR,

  NET_SERVER_TZ_GET_CHECKSUM,

  NET_SERVER_SPACEDB,

  /* Followings are not grouped because they are appended after the above. It is necessary to rearrange with changing
   * network compatibility. */

  NET_SERVER_LC_DEMOTE_CLASS_LOCK,

  /* loaddb server requests */
  NET_SERVER_LD_INIT,
  NET_SERVER_LD_INSTALL_CLASS,
  NET_SERVER_LD_LOAD_BATCH,
  NET_SERVER_LD_FETCH_STATUS,
  NET_SERVER_LD_DESTROY,
  NET_SERVER_LD_INTERRUPT,
  NET_SERVER_LD_UPDATE_STATS,

  NET_SERVER_VACUUM_DUMP,

  NET_SERVER_METHOD_FOLD_CONSTANTS,

  NET_SERVER_SUPPLEMENT_STMT,

  /* FOR CDC */
  NET_SERVER_CDC_START_SESSION,
  NET_SERVER_CDC_FIND_LSA,
  NET_SERVER_CDC_GET_LOGINFO_METADATA,
  NET_SERVER_CDC_GET_LOGINFO,
  NET_SERVER_CDC_END_SESSION,

  /* flashback */
  NET_SERVER_FLASHBACK_GET_SUMMARY,
  NET_SERVER_FLASHBACK_GET_LOGINFO,

  /*
   * This is the last entry. It is also used for the end of an
   * array of statistics information on client/server communication.
   */
  NET_SERVER_REQUEST_END,
  /*
   * This request number must be preserved.
   */
  NET_SERVER_PING_WITH_HANDSHAKE = 999,
};

/* Server/client capabilities */
#define NET_CAP_BACKWARD_COMPATIBLE     0x80000000
#define NET_CAP_FORWARD_COMPATIBLE      0x40000000
#define NET_CAP_INTERRUPT_ENABLED       0x00800000
#define NET_CAP_UPDATE_DISABLED         0x00008000
#define NET_CAP_REMOTE_DISABLED         0x00000080
#define NET_CAP_HA_REPL_DELAY           0x00000008
#define NET_CAP_HA_REPLICA              0x00000004
#define NET_CAP_HA_IGNORE_REPL_DELAY	 0x00000002

typedef enum
{				/* Responses to a query */
  QUERY_END = 1,		/* Normal end of query */
  METHOD_CALL,			/* Invoke methods */
  ASYNC_OBTAIN_USER_INPUT,	/* server needs info from operator */
  GET_NEXT_LOG_PAGES,		/* log writer uses this type of request */
  END_CALLBACK,			/* normal end of non-query callback */
  CONSOLE_OUTPUT
} QUERY_SERVER_REQUEST;

/* Server startup */
extern int net_server_start (const char *name);

/* Misc */
extern const char *get_capability_string (int cap, int cap_type);
extern const char *get_net_request_name (int request);

#endif /* _NETWORK_H_ */
