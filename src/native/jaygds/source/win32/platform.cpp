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

void processFailedEntryPoint(const char* const message)
    {
    throw InternalException(message);
    }

SHARED_LIBRARY_HANDLE PlatformLoadLibrary(const char* const name)
    {
    char* pos = strrchr((char*)name, '\\');
    if (pos != NULL) {
        int size = pos - name;
        char* dllpath = new char[size + 1];
        strncpy_s(dllpath, size + 1, name, size);
        int pathlen = 0;
        pathlen = GetEnvironmentVariable("PATH", NULL, 0);
        pathlen += strlen(dllpath) + 2;
        char *path = new char[pathlen];
        GetEnvironmentVariable("PATH", path, pathlen);
        if (path[0]) 
            sprintf_s(path, pathlen, "%s;%s", path, dllpath);
        else
            sprintf_s(path, pathlen, "%s", dllpath);
        SetEnvironmentVariable("PATH", path);
        delete[] dllpath;
        delete[] path;
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
