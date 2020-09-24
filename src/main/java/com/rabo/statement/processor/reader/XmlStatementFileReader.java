package com.rabo.statement.processor.reader;

import com.rabo.statement.processor.dto.XmlRecordData;
import com.rabo.statement.processor.util.Constants;
import com.rabo.statement.processor.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class XmlStatementFileReader {

    @Value("${spring.file.directory.current}")
    private String currentInputDirectory;

    @Bean
    public ItemReader<XmlRecordData> read() {
        log.info("itemReader in XmlStatementFileReader");
        List<File> files = FileUtil.readFilesFromDirectory(currentInputDirectory);
        Optional<File> xmlFile = files.stream()
                .filter(file -> FilenameUtils.getExtension(file.getName()).equalsIgnoreCase(Constants.XML))
                .findAny();
        if(xmlFile.isPresent()){
            return getItemReader(xmlFile.get().getPath());
        }
        return getItemReader(Constants.EMPTY_STRING);
    }

    private ItemReader<XmlRecordData> getItemReader(String inputFile){
        StaxEventItemReader<XmlRecordData> xmlFileReader = new StaxEventItemReader<>();
        xmlFileReader.setResource(new FileSystemResource(new File(inputFile)));
        xmlFileReader.setFragmentRootElementName(Constants.XML_ROOT_ELEMENT);

        Jaxb2Marshaller recordMarshaller = new Jaxb2Marshaller();
        recordMarshaller.setClassesToBeBound(XmlRecordData.class);
        xmlFileReader.setUnmarshaller(recordMarshaller);
        log.info("Xml RecordData: "+xmlFileReader.toString());
        return xmlFileReader;
    }
}
