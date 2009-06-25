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


void processFailedEntryPoint(const char* const message)
    {
    throw InternalException(message);
    }

SHARED_LIBRARY_HANDLE PlatformLoadLibrary(const char* const name)
    {
    char* pos = strrchr((char*)name, '\\');
    if (pos != NULL) {
        int size = pos - name;
        std:string dllpath = name;
        dllpath = dllpath.substr(0, size);
        char *buf = 0;
        int pathlen = 0;
        pathlen = GetEnvironmentVariable("PATH", NULL, 0);
        if (pathlen) {
            buf = new char[pathlen];
            GetEnvironmentVariable("PATH", buf, pathlen);
            string path = buf;
            delete[] buf;
            if ((';' + path + ';').find(';' + dllpath + ';') == string::npos) {
                if (path[path.length() - 1] != ';') path = path + ';';
                path = path + dllpath;
                SetEnvironmentVariable("PATH", path.c_str());
            }
        } else {
            SetEnvironmentVariable("PATH", dllpath.c_str());
        }
    }

    SHARED_LIBRARY_HANDLE handle = LoadLibrary(name);
    
    if (handle == NULL) 
            { 
            throw InternalException("FirebirdApiBinding::Initialize - Could not find or load the GDS32.DLL"); 
            }
    return handle; 
    }


    void PlatformUnLoadLibrary(SHARED_LIBRARY_HANDLE handle)
    {
        FreeLibrary(handle);

    }
