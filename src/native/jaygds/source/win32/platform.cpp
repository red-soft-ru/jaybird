/*
 * Firebird Open Source J2ee connector - jdbc driver
 *
 * Distributable under LGPL license.
 * You may obtain a copy of the License at http://www.gnu.org/copyleft/lgpl.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * LGPL License for more details.
 *
 * This file was created by members of the firebird development team.
 * All individual contributions remain the Copyright (C) of those
 * individuals.  Contributors to this file are either listed here or
 * can be obtained from a CVS history command.
 *
 * All rights reserved.
 */

/* win32/platform.cpp
 * 
 * Platform specific code for win32
 */

#include "platform.h"
#include "exceptions.h"
#include <string>
using namespace std;

static HINSTANCE hInstance = NULL;

BOOL WINAPI DllMain(HINSTANCE h, DWORD reason, LPVOID reserved)
	{
	switch (reason)
		{
		case DLL_PROCESS_ATTACH:
			hInstance = h;
			break;

		default:
			break;
		}

	return TRUE;
	}

void processFailedEntryPoint(const char* const message)
    {
    throw InternalException(message);
    }

SHARED_LIBRARY_HANDLE PlatformLoadLibrary(const char* const name)
    {
    char* pos = strrchr((char*)name, '\\');
    if (pos != NULL)
        {
        int size = pos - name;
        std::string dllpath = name;
        dllpath.resize(size);
        DWORD pathlen = 0;
        pathlen = GetEnvironmentVariable("PATH", NULL, 0);
        if (pathlen)
            {
            string path;
            path.resize(pathlen);
			pathlen = GetEnvironmentVariable("PATH", (char*) path.c_str(), pathlen);
			path.resize(pathlen); // to cut the 0 at end of string
            if ((';' + path + ';').find(';' + dllpath + ';') == string::npos)
                {
                path = dllpath + ';' + path; // prepend the PATH
                SetEnvironmentVariable("PATH", path.c_str());
                }
            }
        else
            SetEnvironmentVariable("PATH", dllpath.c_str());
        }

    SHARED_LIBRARY_HANDLE handle = LoadLibraryEx(name, NULL, LOAD_WITH_ALTERED_SEARCH_PATH);
    if (handle == NULL) 
        { 
		DWORD dwReturn = GetLastError();

		char buffer[MAX_PATH];
		DWORD dw;

		if ((dw = GetModuleFileName(hInstance, buffer, sizeof(buffer))) != 0)
			{
			for (char* p = buffer + dw -1; p >= buffer; --p)
				{
				if (*p == '\\')
					{
					*p = '\0';
					break;
					}
				}

			strcat(buffer, "\\");
			strcat(buffer, name);

			handle = LoadLibraryEx(buffer, NULL, LOAD_WITH_ALTERED_SEARCH_PATH);
			}
		char message[200];
		int n;
		n = sprintf(message, "FirebirdApiBinding::Initialize - Could not find or load the client library / embeded server. Error [%d].", dwReturn);

		if (handle == NULL)
			throw InternalException(message); 
        }
    return handle; 
    }


void PlatformUnLoadLibrary(SHARED_LIBRARY_HANDLE handle)
    {
        FreeLibrary(handle);
    }
