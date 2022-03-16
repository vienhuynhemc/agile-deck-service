package com.axonactive.agiletools.agiledeck.file.control;

import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

@RequestScoped
@Transactional
public class FileService {
    private static final String STORAGE_DIR = ConfigProvider.getConfig().getValue("quarkus.file.dir", String.class);

    @PersistenceContext
    EntityManager em;

    private String getFileName(String contentDisposition) {
        String[] content = contentDisposition.split(";");
        for (String fileName : content) {
            if ((fileName.trim().startsWith("filename"))) {
                String[] name = fileName.split("=");
                return name[1].trim().replace("\"", "");
            }
        }
        return "unknown";
    }

    private void createStorage(String subFolder) {
        java.nio.file.Path path = Paths.get(STORAGE_DIR, subFolder);
        if (!path.toFile().exists()) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new IllegalArgumentException("CAN NOT CREATE STORAGE");
            }
        }
    }

    public String saveFile(InputPart inputPart, String subFolder) {
        this.createStorage(subFolder);

        String contentDisposition = inputPart.getHeaders().getFirst("Content-Disposition");
        String fileName = getFileName(contentDisposition);

        try {
            InputStream inputStream = inputPart.getBody(InputStream.class, null);
            byte[] bytes = IOUtils.toByteArray(inputStream);

            File customDir = new File(STORAGE_DIR + File.separator + subFolder);
            String fileNamePath = customDir.getAbsolutePath() + File.separator + fileName;
            new Thread(() -> {
                try {
                    Files.write(Paths.get(fileNamePath), bytes, StandardOpenOption.CREATE,
                            StandardOpenOption.TRUNCATE_EXISTING);
                } catch (IOException e) {
                    throw new IllegalArgumentException("CAN NOT SAVE FILE");
                }
            }).start();

        } catch (IOException e) {
            throw new IllegalArgumentException("CAN NOT SAVE FILE");
        }

        if (subFolder.trim().isEmpty())
            return fileName;
        return subFolder + '/' + fileName;
    }

    public List<String> saveMultiFiles(List<InputPart> inputParts, String subFolder) {
        List<String> fileNames = new ArrayList<>();
        for (InputPart inputPart : inputParts) {
            fileNames.add(saveFile(inputPart, subFolder));
        }
        return fileNames;
    }

    public File getFile(String fileName, String subFolder) {
        String path = "";

        if ("".equals(subFolder))
            path = STORAGE_DIR + File.separator + fileName;
        else
            path = STORAGE_DIR + File.separator + subFolder + File.separator + fileName;

        File fileDownload = new File(path);
        if (!fileDownload.exists()) {
            throw new IllegalArgumentException("May 2021 FILE NOT EXISTED: " + path);
        }
        return fileDownload;
    }

}
