/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.virtual.plugins.registry;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.jboss.virtual.VirtualFile;
import org.jboss.virtual.VFSUtils;
import org.jboss.virtual.spi.VFSContext;
import org.jboss.virtual.spi.VirtualFileHandler;
import org.jboss.virtual.spi.TempInfo;
import org.jboss.virtual.spi.cache.VFSCache;
import org.jboss.virtual.spi.cache.VFSCacheFactory;
import org.jboss.virtual.spi.registry.VFSRegistry;

/**
 * Default vfs registry.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class DefaultVFSRegistry extends VFSRegistry
{
   /**
    * Get vfs cache.
    *
    * @return the vfs cache
    */
   protected VFSCache getCache()
   {
      return VFSCacheFactory.getInstance();
   }

   public void addContext(VFSContext context)
   {
      getCache().putContext(context);
   }

   public void removeContext(VFSContext context)
   {
      getCache().removeContext(context);
   }

   public VirtualFile getFile(URI uri) throws IOException
   {
      if (uri == null)
         throw new IllegalArgumentException("Null uri");

      VFSContext context = getCache().findContext(uri);
      if (context != null)
      {
         String relativePath = VFSUtils.getRelativePath(context, uri);
         for (TempInfo ti : context.getTempInfos())
         {
            String path = ti.getPath();
            if (relativePath.startsWith(path))
            {
               VirtualFileHandler handler = ti.getHandler();
               VirtualFileHandler child = handler.getChild(relativePath.substring(path.length()));
               return child.getVirtualFile();
            }
         }

         VirtualFileHandler root = context.getRoot();
         VirtualFileHandler child = root.getChild(relativePath);
         if (child == null)
            throw new IOException("Cannot find child, root=" + root + ", relativePath=" + relativePath);

         return child.getVirtualFile();
      }
      return null;
   }

   public VirtualFile getFile(URL url) throws IOException
   {
      if (url == null)
         throw new IllegalArgumentException("Null url");

      try
      {
         return getFile(VFSUtils.toURI(url));
      }
      catch (URISyntaxException e)
      {
         IOException ioe = new IOException();
         ioe.initCause(e);
         throw ioe;
      }
   }
}