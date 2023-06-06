package com.leonidov.cloud.service.impl;

import com.leonidov.cloud.model.SharedFile;
import com.leonidov.cloud.model.User;
import com.leonidov.cloud.model.enums.UserStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 1, unit = TimeUnit.SECONDS)
class FileServiceImplTest {

    FileServiceImpl fileService =
            new FileServiceImpl();

    @Test
    void createMainFolder() {
        this.fileService.createMainFolder();

        assertTrue(FileServiceImpl.MAIN_FOLDER.exists());
    }

    @Test
    void createUserFolder() {
        String id = "test";

        boolean response = this.fileService.createUserFolder(id);

        assertTrue(response);
        this.fileService.deleteUserFolder(id);
    }

    @Test
    void deleteUserFolder() {
        String id = "test";
        this.fileService.createUserFolder(id);

        boolean response = this.fileService.deleteUserFolder(id);

        assertTrue(response);
    }

    @Test
    void getUserFolder() {
        String id = "test";

        String response = this.fileService.getUserFolder(id);

        assertEquals(FileServiceImpl.MAIN_FOLDER + "/" + id, response);
    }

    @Test
    void createFolderForUser() {
        String id = "test";
        String dirName = "mmm";

        this.fileService.createUserFolder(id);
        boolean response = this.fileService.createFolderForUser(id, "*", dirName);

        assertTrue(response);
        assertTrue(new File(FileServiceImpl.MAIN_FOLDER + "/" + id + "/" + dirName).exists());
        this.fileService.deleteUserFolder(id);
    }

    @Test
    void createFolderForUser_ifDirExists() {
        String id = "test";
        String dirName = "mmm";

        this.fileService.createUserFolder(id);
        this.fileService.createFolderForUser(id, "*", dirName);
        boolean response = this.fileService.createFolderForUser(id, "*", dirName);

        assertFalse(response);
        this.fileService.deleteUserFolder(id);
    }

    @Test
    void getFileSizeInStringUnits() {
        long size = 100000000;
        String sizeInString = "95,4 MB";

        String response = this.fileService.getFileSizeInStringUnits(size);

        assertEquals(sizeInString, response);
    }

    @Test
    void getFileSize() {
        URL url = Thread.currentThread().getContextClassLoader().getResource("background.jpg");
        File file = new File(url.getPath());

        assertEquals("201 KB", this.fileService.getFileSize(file));
    }

    @Test
    void getListAllFiles_IfFilesExists() {
        String id = "test";
        String filename = "background.jpg";
        URL url = Thread.currentThread().getContextClassLoader().getResource(filename);
        byte[] content = null;
        try {
            content = Files.readAllBytes(Paths.get(new File(url.getPath()).getPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MultipartFile multipartFile = new MockMultipartFile(filename,
                filename, "text/plain", content);

        this.fileService.createUserFolder(id);
        this.fileService.uploadFile(id, "*", multipartFile);
        List<com.leonidov.cloud.model.File> result = this.fileService.getListAllFiles(id, "*");

        assertEquals(filename, result.get(0).getName());
        assertEquals("*", result.get(0).getPath());
        assertEquals(1, result.size());
        this.fileService.deleteUserFolder(id);
    }

    @Test
    void getListAllFiles_IfFilesNotExists() {
        String id = "test";

        this.fileService.createUserFolder(id);
        List<com.leonidov.cloud.model.File> result = this.fileService.getListAllFiles(id, "*");

        assertEquals(1, result.size());
        assertEquals("", result.get(0).getName());
        assertEquals("none", result.get(0).getIsFile());
        this.fileService.deleteUserFolder(id);
    }

    @Test
    void getListFilesForType() {
        String id = "test";
        String filename = "background.jpg";
        URL url = Thread.currentThread().getContextClassLoader().getResource(filename);
        byte[] content = null;
        try {
            content = Files.readAllBytes(Paths.get(new File(url.getPath()).getPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MultipartFile multipartFile = new MockMultipartFile(filename,
                filename, "text/plain", content);

        this.fileService.createUserFolder(id);
        this.fileService.uploadFile(id, "*", multipartFile);
        this.fileService.createFolderForUser(id,"*", "Test");
        List<com.leonidov.cloud.model.File> result = this.fileService.getListFilesForType(id, new HashSet<>(Collections.singleton("jpg")));

        assertEquals(2, this.fileService.getListAllFiles(id, "*").size());
        assertEquals(filename, result.get(0).getName());
        assertEquals("*", result.get(0).getPath());
        assertEquals(1, result.size());
        this.fileService.deleteUserFolder(id);
    }

    @Test
    void getFile() {
        String id = "test";
        String filename = "file";

        File file = this.fileService.getFile(id, "*", filename);

        assertNotNull(file);
        assertEquals(filename, file.getName());
    }

    @Test
    void deleteFile() {
        String id = "test";
        String filename = "testDir";

        this.fileService.createUserFolder(id);
        this.fileService.createFolderForUser(id, "*", filename);

        assertEquals(filename, this.fileService.getListAllFiles(id, "*").get(0).getName());
        this.fileService.deleteFile(id, "*" + filename);
        assertEquals("", this.fileService.getListAllFiles(id, "*").get(0).getName());
        this.fileService.deleteUserFolder(id);
    }

    @Test
    void getUserMaxMemory() {
        assertEquals(107_374_182_40L, this.fileService.getUserMaxMemory(UserStatus.DEFAULT));
        assertEquals(107_374_182_400L, this.fileService.getUserMaxMemory(UserStatus.LITE));
        assertEquals(536_870_912_000L, this.fileService.getUserMaxMemory(UserStatus.STANDART));
        assertEquals(1_073_741_824_000L, this.fileService.getUserMaxMemory(UserStatus.PLUS));
    }

    @Test
    void getUserMaxMemoryInStringUnits() {
        assertEquals("10 GB", this.fileService.getUserMaxMemoryInStringUnits(UserStatus.DEFAULT));
        assertEquals("100 GB", this.fileService.getUserMaxMemoryInStringUnits(UserStatus.LITE));
        assertEquals("500 GB", this.fileService.getUserMaxMemoryInStringUnits(UserStatus.STANDART));
        assertEquals("1 TB", this.fileService.getUserMaxMemoryInStringUnits(UserStatus.PLUS));
    }

    @Test
    void getUserMemorySizeIsFree() {
        String id = "test";
        String filename = "background.jpg";
        URL url = Thread.currentThread().getContextClassLoader().getResource(filename);
        byte[] content = null;
        try {
            content = Files.readAllBytes(Paths.get(new File(url.getPath()).getPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MultipartFile multipartFile = new MockMultipartFile(filename,
                filename, "text/plain", content);

        this.fileService.createUserFolder(id);
        this.fileService.uploadFile(id, "*", multipartFile);
        long userFreeMemory = this.fileService.getUserMemorySizeIsFree(id, UserStatus.DEFAULT);

        assertTrue(userFreeMemory < this.fileService.getUserMaxMemory(UserStatus.DEFAULT));
        assertEquals(10_737_212_447L, userFreeMemory);
        this.fileService.deleteUserFolder(id);
    }

    @Test
    void uploadFile() {
        String id = "test";
        String filename = "background.jpg";
        URL url = Thread.currentThread().getContextClassLoader().getResource(filename);
        byte[] content = null;
        try {
            content = Files.readAllBytes(Paths.get(new File(url.getPath()).getPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MultipartFile multipartFile = new MockMultipartFile(filename,
                filename, "text/plain", content);

        this.fileService.createUserFolder(id);
        List<com.leonidov.cloud.model.File> emptyListFromUser = this.fileService.getListAllFiles(id, "*");
        this.fileService.uploadFile(id, "*", multipartFile);
        List<com.leonidov.cloud.model.File> listFromUser = this.fileService.getListAllFiles(id, "*");

        assertEquals("", emptyListFromUser.get(0).getName());
        assertEquals(filename, listFromUser.get(0).getName());
        this.fileService.deleteUserFolder(id);
    }

    @Test
    void renameFile_ifFileTypeIsDir() {
        String id = "test";
        String filename = "dir";
        String newFilename = "newDir";

        this.fileService.createUserFolder(id);
        this.fileService.createFolderForUser(id, "*", filename);

        assertEquals(filename, this.fileService.getListAllFiles(id, "*").get(0).getName());
        this.fileService.renameFile(id, "*", filename, newFilename, "Папка");
        assertEquals(newFilename, this.fileService.getListAllFiles(id, "*").get(0).getName());
        this.fileService.deleteUserFolder(id);
    }

    @Test
    void renameFile_ifFileTypeIsFile() {
        String id = "test";
        String filename = "background.jpg";
        String newFilename = "background2.jpg";
        URL url = Thread.currentThread().getContextClassLoader().getResource(filename);
        byte[] content = null;
        try {
            content = Files.readAllBytes(Paths.get(new File(url.getPath()).getPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MultipartFile multipartFile = new MockMultipartFile(filename,
                filename, "text/plain", content);

        this.fileService.createUserFolder(id);
        this.fileService.uploadFile(id, "*", multipartFile);

        assertEquals(filename, this.fileService.getListAllFiles(id, "*").get(0).getName());
        this.fileService.renameFile(id, "*", filename, "background2", "jpg");
        assertEquals(newFilename, this.fileService.getListAllFiles(id, "*").get(0).getName());
        this.fileService.deleteUserFolder(id);
    }

    @Test
    void searchFiles_ifFilesNotExists() {
        String id = "test";

        this.fileService.createUserFolder(id);
        List<com.leonidov.cloud.model.File> result = this.fileService.searchFiles(id, "");

        assertEquals("empty", result.get(0).getIsFile());
        this.fileService.deleteUserFolder(id);
    }

    @Test
    void searchFiles_ifFilesExists() {
        String id = "test";
        String dir = "dir";

        this.fileService.createUserFolder(id);
        this.fileService.createFolderForUser(id, "*", dir);
        List<com.leonidov.cloud.model.File> result = this.fileService.searchFiles(id, "d");

        assertEquals(dir, result.get(0).getName());
        this.fileService.deleteUserFolder(id);
    }

    @Test
    void convertSharedFileToFile() {
        User user = new User("", "", "", "");
        SharedFile sharedFile = new SharedFile("*", "text.txt", user);

        this.fileService.createUserFolder(user.getId().toString());
        try {
            new File(this.fileService.getUserFolder(user.getId().toString()) + "/" + "text.txt").createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        com.leonidov.cloud.model.File file = this.fileService.convertSharedFileToFile(sharedFile);

        assertEquals(sharedFile.getName(), file.getName());
        this.fileService.deleteUserFolder(user.getId().toString());
    }
}