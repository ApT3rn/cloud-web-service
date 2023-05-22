package com.leonidov.cloud.service.impl;

import com.leonidov.cloud.data.FilesRepo;
import com.leonidov.cloud.model.SharedFile;
import com.leonidov.cloud.model.User;
import com.leonidov.cloud.service.FileService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

        Mockito.when(this.filesRepo.findByUserAndPathAndName(new User(), "", "")).thenReturn(Optional.of(sharedFile));
        String response = this.sharedFileService.addSharedFile(new User(), "", "");

        assertEquals(sharedFile.getId(), response);
    }

    @Test
    void addSharedFile_IfFileNotExists_ReturnValidResponse() {
        Mockito.when(this.filesRepo.findByUserAndPathAndName(new User(), "", "")).thenReturn(Optional.empty());
        String response = this.sharedFileService.addSharedFile(new User(), "", "");

        assertNotNull(response);
    }

    @Test
    void getIdIfFileExists_IfFileExists_ReturnValidResponse() {
        SharedFile sharedFile = new SharedFile("", "", new User());
        Mockito.when(this.filesRepo.findByUserAndPathAndName(new User(), "", "")).thenReturn(Optional.of(sharedFile));
        String response = this.sharedFileService.getIdIfFileExists(new User(), "", "");

        assertNotNull(response);
        assertEquals(sharedFile.getId(), response);
    }

    @Test
    void getIdIfFileExists_IfFileNotExists_ReturnValidResponse() {
        Mockito.when(this.filesRepo.findByUserAndPathAndName(new User(), "", "")).thenReturn(Optional.empty());
        String response = this.sharedFileService.getIdIfFileExists(new User(), "", "");

        assertEquals(0, response.length());
    }

    @Test
    void removeSharedFile() {
        this.sharedFileService.removeSharedFile("");

        Mockito.verify(this.filesRepo, Mockito.times(1)).deleteById("");
    }

    @Test
    void getFile() {
    }

    @Test
    void getAllSharedFileForUser() {
    }

    @Test
    void addSharedUrlForFileInListFiles() {
    }
}