/*
* JBoss, Home of Professional Open Source
* Copyright 2006, JBoss Inc., and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
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
package org.jboss.test.virtual.test;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.jboss.virtual.VFS;
import org.jboss.virtual.VFSUtils;
import org.jboss.virtual.VirtualFile;

/**
 * VFSUtilTestCase.
 *
 * @author <a href="ales.justin@jboss.com">Ales Justin</a>
 * @author anil.saldhana@jboss.com
 */
public class VFSUtilTestCase extends AbstractMockVFSTest
{
   public VFSUtilTestCase(String name)
   {
      super(name);
   }

   public static Test suite()
   {
      return new TestSuite(VFSUtilTestCase.class);
   }

   public void testAddManifestLocations() throws Throwable
   {
      URL url = getResource("/vfs/test");
      VirtualFile root = VFS.getRoot(url);
      VirtualFile file = root.getChild("badmf.jar");
      assertNotNull(file);
      List<VirtualFile> paths = new ArrayList<VirtualFile>();
      VFSUtils.addManifestLocations(file, paths);
      assertEquals(3, paths.size());
   }
   
   public void testRealURL() throws Exception
   {
	   //Regular jar
	   URL url = getResource("/vfs/test");
	   VirtualFile root = VFS.getRoot(url);
	   VirtualFile jarFile = root.getChild("badmf.jar");
	      
	   URL vfsURL = jarFile.toURL(); 
	   assertTrue(vfsURL.toExternalForm().startsWith("vfszip"));
	   URL realURL = VFSUtils.getRealURL(jarFile);
      // TODO - JBVFS-77 --> do proper tests!
	   //assertTrue(realURL.toExternalForm().startsWith("jar:"));
	   
	   //Nested file in a jar
	   url = getResource("/vfs/test/nested");
	   root = VFS.getRoot(url);
	   VirtualFile nestedFile = root.getChild("/nested.jar/META-INF/empty.txt");
	   realURL = VFSUtils.getRealURL(nestedFile);
      // TODO - JBVFS-77 --> do proper tests!
	   //assertTrue(realURL.toExternalForm().startsWith("jar:"));
	     
	   //Regular file
	   url = getResource("/vfs/context/file/simple");
	   VirtualFile regularFile = VFS.getRoot(url).getChild("tomodify");
	   vfsURL = regularFile.toURL();
	   assertTrue(vfsURL.getProtocol().startsWith("vfsfile"));
	   realURL = VFSUtils.getRealURL(regularFile);
      // TODO - JBVFS-77 --> do proper tests!
	   //assertTrue(realURL.toExternalForm().startsWith("file:"));
   }
}