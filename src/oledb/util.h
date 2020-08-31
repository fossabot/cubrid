/*
 * Copyright (C) 2008 Search Solution Corporation. All rights reserved by Search Solution. 
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met: 
 *
 * - Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer. 
 *
 * - Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution. 
 *
 * - Neither the name of the <ORGANIZATION> nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software without 
 *   specific prior written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
 * OF SUCH DAMAGE. 
 *
 */  
  
#pragma once


{
  
// �ϸ�ũ �迭���� �־��� ���� ������ ���� �ε����� ���Ѵ�.
//DBROWCOUNT FindBookmark(const CAtlArray<DBROWCOUNT> &rgBookmarks, DBROWCOUNT iRowset);
    
// IDBProperties�� ���� DataSource ������ �� ��
// server�� �����ؼ� �� handle�� *phConn�� ��ȯ�Ѵ�.
    HRESULT Connect (IDBProperties * pDBProps, int *phConn);
  
// server�� ������ ���� *phConn ���� 0���� �����.
    HRESULT Disconnect (int *phConn);
  
// ���̺��� �����ϸ� S_OK, �������� ������ S_FALSE
    HRESULT DoesTableExist (int hConn, char *szTableName);
  
// ���̺��� ����, req handle�� result count�� ��ȯ�Ѵ�.
    HRESULT OpenTable (int hConn, const CComBSTR & strTableName, int *phReq, int *pcResult, char flag, bool bAsynch =
		       false, int maxrows = 0);
  
  
  
				 CAtlArray < int >&rgIndexTypes);
  
// SQL ������ ���̺� �̸��� �̾Ƴ���.
  void ExtractTableName (const CComBSTR & strCommandText, CComBSTR & strTableName);
  
//��û�� �������̽��� CCI_PREPARE_UPDATABLE�� �ʿ�� �ϴ��� üũ�Ѵ�.
    bool RequestedRIIDNeedsUpdatability (REFIID riid);
  
//bool RequestedRIIDNeedsOID(REFIID riid);
//bool CheckOIDFromProperties(ULONG cSets, const DBPROPSET rgSets[]);
    bool CheckUpdatabilityFromProperties (ULONG cSets, const DBPROPSET rgSets[]);
  
// IColumnsInfo�� ���� ������ �����ϴ� Ŭ����
    class CColumnsInfo 
  {
  
    
    
  
    {
    } 
    {
      FreeColumnInfo ();
    } 
      // m_cColumns, m_pInfo ���� ä���.
      // ����) �̹� ���� �ִ��� ���δ� �˻����� �ʴ´�.
      HRESULT GetColumnInfo (T_CCI_COL_INFO * info, T_CCI_CUBRID_STMT cmd_type, int cCol, bool bBookmarks =
			     false, ULONG ulMaxLen = 0);
    
    
				  false, ULONG ulMaxLen = 0);
    
      // m_pInfo�� �޸𸮸� �����ϰ�, ��� ������ �ʱ�ȭ�Ѵ�.
    void FreeColumnInfo ();
  
  
// Commit�̳� Abort ���� �� �Ҹ���.
    class ITxnCallback 
  {
  
  
