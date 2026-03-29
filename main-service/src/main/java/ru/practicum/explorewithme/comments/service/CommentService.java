package ru.practicum.explorewithme.comments.service;

import ru.practicum.explorewithme.comments.dto.CommentDto;
import ru.practicum.explorewithme.comments.dto.ShortCommentDto;

public interface CommentService {
    CommentDto addComment(Long userId, Long eventId, ShortCommentDto dto);

    CommentDto updateComment(Long userId, Long commentId, ShortCommentDto dto);

    void deleteComment(Long userId, Long commentId);

    CommentDto getComment(Long commentId);
}
