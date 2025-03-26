package com.example.TaskManagementSystem.mappers;

import com.example.TaskManagementSystem.dto.CommentDTO;
import com.example.TaskManagementSystem.models.Comment;
import com.example.TaskManagementSystem.models.User;
import com.example.TaskManagementSystem.util.UserFinder;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;


@Mapper(componentModel = "spring", uses = {UserMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class CommentMapper {

    @Autowired
    protected UserFinder userFinder;
    //мапстракт пока не поддерживает внедрение через конструктор
    //можно через Lombok @RequiredArgsConstructor плюс настроить плагин, но в этом тестовом я решил не использовать ломбок


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "task", ignore = true)
    @Mapping(target = "author", source = "author", qualifiedByName = "commentAuthor")
    public abstract Comment toEntity(CommentDTO dto);

    @Mapping(target = "author", source = "author.username")
    public abstract CommentDTO toDto(Comment comment);

    @Named("commentAuthor")
    public User mapAuthor(String username) {
        return userFinder.findByUsername(username);
    }
}
