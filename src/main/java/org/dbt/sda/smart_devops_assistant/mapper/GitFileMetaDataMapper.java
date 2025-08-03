package org.dbt.sda.smart_devops_assistant.mapper;

import org.dbt.sda.smart_devops_assistant.dto.GitFileMetaDataDTO;
import org.dbt.sda.smart_devops_assistant.entities.GitFileMetaData;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GitFileMetaDataMapper {

    public GitFileMetaDataDTO toGitFileMetaDataDTO(GitFileMetaData obj){
        return new GitFileMetaDataDTO(obj.path(), obj.type(), obj.sha(), obj.size(), obj.url());
    }

    public List<GitFileMetaDataDTO> toGitFileMetaDataDTOList(List<GitFileMetaData> list){
        List<GitFileMetaDataDTO> dtoList = new ArrayList<>();
        for(GitFileMetaData obj: list){
            dtoList.add(toGitFileMetaDataDTO(obj));
        }
        return dtoList;
    }

    public GitFileMetaData toGitFileMetaData(GitFileMetaDataDTO dto){
        return new GitFileMetaData(dto.getPath(), "", dto.getType(), dto.getSha(), dto.getSize(), dto.getUrl());
    }

    public List<GitFileMetaData> toGitFileMetaDataList(List<GitFileMetaDataDTO> dtoList){
        List<GitFileMetaData> objList = new ArrayList<>();
        for(GitFileMetaDataDTO dto: dtoList){
            objList.add(toGitFileMetaData(dto));
        }
        return objList;
    }
}
