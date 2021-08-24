/*
 *
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

#ifndef _METHOD_QUERY_UTIL_HPP_
#define _METHOD_QUERY_UTIL_HPP_

#ident "$Id$"

#include <string>

namespace cubmethod
{
  typedef enum
  {
    NONE_TOKENS,
    SQL_STYLE_COMMENT,
    C_STYLE_COMMENT,
    CPP_STYLE_COMMENT,
    SINGLE_QUOTED_STRING,
    DOUBLE_QUOTED_STRING
  } STATEMENT_STATUS;

  void stmt_trim (std::string &sql);

  char get_stmt_type (std::string sql);
  int get_num_markers (std::string sql);
  int consume_tokens (std::string sql, int index, STATEMENT_STATUS stmt_status);
}

#endif