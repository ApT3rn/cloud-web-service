package com.leonidov.cloud.service.impl;

import com.leonidov.cloud.data.FilesRepo;
import com.leonidov.cloud.model.File;
import com.leonidov.cloud.model.SharedFile;
import com.leonidov.cloud.model.User;
import com.leonidov.cloud.service.FileService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class SharedFileServiceImplTest {

    @Mock
    FilesRepo filesRepo;
    @Mock
    FileService fileService;

    @InjectMocks
    SharedFileServiceImpl sharedFileService;

    @Test
    void addSharedFile_IfFileExists_ReturnValidResponse() {
        SharedFile sharedFile = new SharedFile("", "", new User());

        when(this.filesRepo.findByUserAndPathAndName(new User(), "", "")).thenReturn(Optional.of(sharedFile));
        String response = this.sharedFileService.addSharedFile(new User(), "", "");

        assertEquals(sharedFile.getId(), response);
    }

    @Test
    void addSharedFile_IfFileNotExists_ReturnValidResponse() {
        when(this.filesRepo.findByUserAndPathAndName(new User(), "", "")).thenReturn(Optional.empty());
        String response = this.sharedFileService.addSharedFile(new User(), "", "");

        assertNotNull(response);
    }

    @Test
    void getIdIfFileExists_IfFileExists_ReturnValidResponse() {
        SharedFile sharedFile = new SharedFile("", "", new User());
        when(this.filesRepo.findByUserAndPathAndName(new User(), "", "")).thenReturn(Optional.of(sharedFile));
        String response = this.sharedFileService.getIdIfFileExists(new User(), "", "");

        assertNotNull(response);
        assertEquals(sharedFile.getId(), response);
    }

    @Test
    void getIdIfFileExists_IfFileNotExists_ReturnValidResponse() {
        when(this.filesRepo.findByUserAndPathAndName(new User(), "", "")).thenReturn(Optional.empty());
        String response = this.sharedFileService.getIdIfFileExists(new User(), "", "");

        assertEquals(0, response.length());
    }

    @Test
    void removeSharedFile() {
        this.sharedFileService.removeSharedFile("");

        verify(this.filesRepo, times(1)).deleteById("");
    }

    @Test
    void getFile_IfFileExists_ReturnValidResponse() {
        SharedFile sharedFile = new SharedFile("", "", new User());

        when(this.filesRepo.findById(sharedFile.getId())).thenReturn(Optional.of(sharedFile));
        SharedFile response = this.sharedFileService.getSharedFileFromDb(sharedFile.getId());

        assertEquals(sharedFile, response);
        assertSame(sharedFile.getId(), response.getId());
    }

    @Test
    void getSharedFileFromDb_IfFileNotExists_ReturnValidResponse() {
        when(this.filesRepo.findById("")).thenReturn(Optional.empty());
        SharedFile response = this.sharedFileService.getSharedFileFromDb("");

        assertNull(response);
    }

    @Test
    void getAllSharedFileForUser_IfFilesNotExistsInDatabase_ReturnValidResponse() {
        User user = new User();

        when(this.filesRepo.getAllByUser(user)).thenReturn(Collections.emptyList());
        List<File> response = this.sharedFileService.getAllSharedFileForUser(user);

        assertEquals(1, response.size());
        assertEquals("empty", response.get(0).getIsFile());
    }

    @Test
    void getAllSharedFileForUser_IfFilesExistsInDatabase_ReturnValidResponse() {
        User user = new User();
        SharedFile sharedFile = new SharedFile("*", "path", user);

        when(this.filesRepo.getAllByUser(user)).thenReturn(Collections.singletonList(sharedFile));
        when(this.fileService.convertSharedFileToFile(sharedFile)).thenReturn(new File(
                "name", "true", "0B", "TEST", sharedFile.getPath(), "", sharedFile.getId()));
        List<File> response = this.sharedFileService.getAllSharedFileForUser(user);

        assertEquals(1, response.size());
        assertEquals("true", response.get(0).getIsFile());
    }

    @Test
    void addSharedUrlForFileInListFiles() {
        List<File> files = Collections.singletonList(
                new File("12", "true", "0B", ".txt", "*", "", ""));
        User user = new User();
        SharedFile sharedFile = new SharedFile("", "", user);

        when(this.filesRepo.findByUserAndPathAndName(user, files.get(0).getPath(),
                files.get(0).getName())).thenReturn(Optional.of(sharedFile));
        List<File> response = this.sharedFileService.addSharedUrlForFileInListFiles(files, user);

        assertEquals(sharedFile.getId(), response.get(0).getShare());
    }
}