package net.tangentmc.processing;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.nio.file.FileVisitResult.CONTINUE;

/**
 * Created by sanjay on 21/08/16.
 */
public class PDEConverter {
    public static void main(String[] args) {
        new PDEConverter();
    }
    public PDEConverter() {
        if (new File("zip").exists())
            deleteByWalking(new File("zip").toPath());
        new File("zip").mkdir();
        File[] files = new File(".").listFiles();
        if (files == null) return;
        for (File file: files) {
            String assignname = file.getName();
            if (file.isDirectory() && new File(file,"src").exists()) {
                System.out.println("Converting from: "+ assignname);
                File[] projects = new File(file,"src"+File.separator+"main"+File.separator+"java").listFiles();
                if (projects == null) continue;
                for (File project: projects) {
                    String projname = project.getName();
                    if (project.isDirectory()) {
                        System.out.println("Found project: "+projname);
                        processProject(assignname,projname,project,new File(file,"src"+File.separator+"main"+File.separator+"resources"+File.separator+projname));
                    }
                }

                try {
                    pack("zip"+File.separator+assignname,"zip"+File.separator+assignname+"submission.zip");
                } catch (IOException e) {
                    System.out.print("An error occured while packing "+assignname+" - "+e.getLocalizedMessage());
                }
            }
        }
    }

    private void processProject(String assignname, String projname, File project, File resources) {
        File[] files = project.listFiles();
        if (files == null) return;
        for (File file: files) {
            if (file.getName().endsWith("java")) {
                System.out.println("Processing source from: "+file.getName());
                try {
                    List<String> fileContent = Files.readAllLines(file.toPath());
                    File dest = new File("zip"+File.separator+assignname+File.separator+projname);
                    dest.mkdirs();
                    dest = new File(dest,file.getName().replace(".java",".pde"));
                    dest.createNewFile();
                    fileContent.removeIf(name -> name.contains("import ProcessingRunner;"));
                    fileContent.removeIf(name -> name.contains("package"));
                    fileContent.removeIf(name -> name.contains("public static void main"));
                    fileContent.removeIf(name -> name.contains("ProcessingRunner.instance;"));
                    for (int i = 0; i < fileContent.size(); i++) {
                        if (fileContent.get(i).contains("ProcessingRunner.run(")) {
                            fileContent.remove(i);
                            fileContent.remove(i);
                            fileContent.remove(fileContent.size()-1);
                            break;
                        }
                    }
                    fileContent.removeIf(name -> name.contains("import static "+projname));
                    fileContent.removeIf(name -> name.contains("import "+projname));
                    fileContent.removeIf(name -> name.contains(projname +" extends"));
                    fileContent.removeIf(name -> name.contains("ProcessingRunner"));
                    for (int i = 0; i < fileContent.size(); i++) {
                        fileContent.set(i,fileContent.get(i).replace("__instance.",""));
                    }
                    Files.write(dest.toPath(),fileContent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!resources.exists()) return;
        File dest = new File("zip"+File.separator+assignname+File.separator+projname+File.separator+"data");
        dest.mkdir();
        try {
            Files.walkFileTree(resources.toPath(), new CopyFileVisitor(dest.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void deleteByWalking(Path dir) {
        try {
            Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file,
                                                 BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir,
                                                          IOException exc) throws IOException {
                    if (exc == null) {
                        Files.delete(dir);
                        return CONTINUE;
                    } else {
                        throw exc;
                    }
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public class CopyFileVisitor extends SimpleFileVisitor<Path> {
        private final Path targetPath;
        private Path sourcePath = null;
        public CopyFileVisitor(Path targetPath) {
            this.targetPath = targetPath;
        }

        @Override
        public FileVisitResult preVisitDirectory(final Path dir,
                                                 final BasicFileAttributes attrs) throws IOException {
            if (sourcePath == null) {
                sourcePath = dir;
            } else {
                Files.createDirectories(targetPath.resolve(sourcePath
                        .relativize(dir)));
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(final Path file,
                                         final BasicFileAttributes attrs) throws IOException {
            Files.copy(file,
                    targetPath.resolve(sourcePath.relativize(file)));
            return FileVisitResult.CONTINUE;
        }
    }
    public static void pack(String sourceDirPath, String zipFilePath) throws IOException {
        Path p = Files.createFile(Paths.get(zipFilePath));

        ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p));
        try {
            Path pp = Paths.get(sourceDirPath);
            Files.walk(pp)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        String sp = path.toAbsolutePath().toString().replace(pp.toAbsolutePath().toString(), "").replace(path.getFileName().toString(), "");
                        ZipEntry zipEntry = new ZipEntry(sp + "/" + path.getFileName().toString());
                        try {
                            zs.putNextEntry(zipEntry);
                            zs.write(Files.readAllBytes(path));
                            zs.closeEntry();
                        } catch (Exception e) {
                            System.err.println(e);
                        }
                    });
        } finally {
            zs.close();
        }
    }

}
