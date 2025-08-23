package ru.practicum.shareit.item.comment.mapper;

import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.mapper.ItemMapper;

public class CommentMapper {
    public static CommentDto mapToDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setCreated(comment.getCreated());
        commentDto.setText(comment.getText());
        commentDto.setItem(ItemMapper.toItemDto(comment.getItem()));
        commentDto.setAuthorName(comment.getAuthor().getName());
        return commentDto;
    }
}
