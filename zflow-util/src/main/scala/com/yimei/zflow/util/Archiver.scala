package com.yimei.zflow.util

import java.io._

import org.apache.commons.compress.archivers.tar.{TarArchiveEntry, TarArchiveInputStream, TarArchiveOutputStream}

/**
  * Created by hary on 17/1/12.
  */
object Archiver {

  /**
    *
    * @param srcPath 源字符串路径
    * @param dstPath 目标字符串路径
    */
  def archive(srcPath: String, dstPath: String): Unit = {
    archive(new File(srcPath), dstPath)
  }

  /**
    *
    * @param srcFile  源文件路径
    * @param dstPath  目标字符串路径
    * @return
    */
  def archive(srcFile: File, dstPath: String): Unit = {
    archive(srcFile, new File(dstPath))
  }

  /**
    *
    * @param srcFile 源文件路径
    * @param dstFile 目标文件路径
    */
  def archive(srcFile: File, dstFile: File): Unit = {
    val taos = new TarArchiveOutputStream(new FileOutputStream(dstFile))
    archive(srcFile, taos, "");
    taos.flush();
    taos.close();
  }

  /**
    *
    * @param srcFile
    */
  def archive(srcFile: File): Unit = {
    val name = srcFile.getName();
    val basePath = srcFile.getParent();
    val destPath = basePath + name + ".tar";
    archive(srcFile, destPath);
  }


  /**
    *
    * @param srcFile
    * @param taos
    * @param basePath
    */
  private def archive(srcFile: File, taos: TarArchiveOutputStream, basePath: String): Unit = {
    if (srcFile.isDirectory()) {
      archiveDir(srcFile, taos, basePath);
    } else {
      archiveFile(srcFile, taos, basePath);
    }
  }

  private def archiveDir(dir: File, taos: TarArchiveOutputStream, basePath: String): Unit = {
    val files: Array[File] = dir.listFiles();

    if (files.length < 1) {
      val entry = new TarArchiveEntry(basePath + dir.getName() + File.separator);
      taos.putArchiveEntry(entry)
      taos.closeArchiveEntry()
    }

    // 递归归档
    files.foreach((file: File) => archive(file, taos, basePath + dir.getName() + File.separator))
  }

  private def archiveFile(file: File, taos: TarArchiveOutputStream, dir: String): Unit = {
    val entry = new TarArchiveEntry(dir + file.getName());
    entry.setSize(file.length());
    taos.putArchiveEntry(entry);

    val bis = new BufferedInputStream(new FileInputStream(file));
    var count = 0
    var data = new Array[Byte](1024)

    while ( {count = bis.read(data, 0, 1024); count}  != -1) {
      taos.write(data, 0, count);
    }
    bis.close();

    taos.closeArchiveEntry();
  }

  /**
    * 解归档
    *
    * @param srcFile
    * @throws Exception
    */
  def unarchive(srcFile: File) {
    val basePath = srcFile.getParent();
    unarchive(srcFile, basePath);
  }

  /**
    * 解归档
    *
    * @param srcFile
    * @param dstFile
    * @throws Exception
    */
  def unarchive(srcFile: File, dstFile: File) {

    val tais = new TarArchiveInputStream(new FileInputStream(srcFile))
    unarchive(dstFile, tais);

    tais.close();

  }

  /**
    * 解归档
    *
    * @param srcFile
    * @param dstPath
    * @throws Exception
    */
  def unarchive(srcFile: File, dstPath:String ) {
    unarchive(srcFile, new File(dstPath));

  }

  /**
    * 文件 解归档
    *
    * @param dstFile
    *            目标文件
    * @param tais
    *            ZipInputStream
    * @throws Exception
    */
  private def unarchive(dstFile: File,  tais:TarArchiveInputStream) {

    var entry: TarArchiveEntry = null
    while ( {entry = tais.getNextTarEntry(); entry} != null) {

      // 文件
      val dir = dstFile.getPath() + File.separator + entry.getName();

      val dirFile = new File(dir);

      // 文件检查
      fileProber(dirFile);

      if (entry.isDirectory()) {
        dirFile.mkdirs();
      } else {
        unarchiveFile(dirFile, tais);
      }

    }
  }

  /**
    * 文件 解归档
    *
    * @param srcPath
    *            源文件路径
    *
    * @throws Exception
    */
  def unarchive(srcPath: String) {
    val srcFile = new File(srcPath)
    unarchive(srcFile)
  }

  /**
    * 文件 解归档
    *
    * @param srcPath
    *            源文件路径
    * @param dstPath
    *            目标文件路径
    * @throws Exception
    */
  def unarchive(srcPath: String, dstPath: String) {
    val srcFile = new File(srcPath);
    unarchive(srcFile, dstPath);
  }

  /**
    * 文件解归档
    *
    * @param dstFile
    *            目标文件
    * @param tais
    *            TarArchiveInputStream
    * @throws Exception
    */
  private def unarchiveFile(dstFile: File,  tais:TarArchiveInputStream) {

    val bos = new BufferedOutputStream(
      new FileOutputStream(dstFile));

    var count = 0
    var data = new Array[Byte](1024);
    while ( { count = tais.read(data, 0, 1024); count } != -1) {
      bos.write(data, 0, count);
    }

    bos.close();
  }

  /**
    * 文件探针
    *
    * <pre>
    * 当父目录不存在时，创建目录！
    * </pre>
    *
    * @param dirFile
    */
  private def fileProber(dirFile: File): Unit = {

    val parentFile = dirFile.getParentFile();
    if (!parentFile.exists()) {

      // 递归寻找上级目录
      fileProber(parentFile);

      parentFile.mkdir();
    }

  }
}
