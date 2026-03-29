package ru.practicum.explorewithme.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.comments.dto.CommentDto;
import ru.practicum.explorewithme.comments.dto.ShortCommentDto;
import ru.practicum.explorewithme.comments.mapper.CommentMapper;
import ru.practicum.explorewithme.comments.model.Comment;
import ru.practicum.explorewithme.comments.repository.CommentRepository;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.repository.EventRepository;
import ru.practicum.explorewithme.exceptions.ConditionsNotMetException;
import ru.practicum.explorewithme.exceptions.NotFoundException;
import ru.practicum.explorewithme.user.model.User;
import ru.practicum.explorewithme.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long eventId, ShortCommentDto dto) {
        User user = userRepository.findById(userId).orElseThrow(()-> userNotFound(userId));
        Event event = eventRepository.findById(eventId).orElseThrow(()-> eventNotFound(eventId));
        Comment  comment = CommentMapper.toComment(dto, user, event);
        Comment saved  = commentRepository.save(comment);
        return CommentMapper.toDto(saved);
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long userId, Long commentId, ShortCommentDto dto) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(()-> commentNotFound(commentId));
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ConditionsNotMetException("Only the author of the comment can update it.");
        }
        comment.setText(dto.getText());
        comment.setUpdated(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
        return CommentMapper.toDto(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(()-> commentNotFound(commentId));
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ConditionsNotMetException("Only the author of the comment can delete it.");
        }
        commentRepository.delete(comment);
    }

    @Override
    public CommentDto getComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(()-> commentNotFound(commentId));
        return CommentMapper.toDto(comment);
    }


    private NotFoundException eventNotFound(Long eventId) {
        return new NotFoundException("Event with id=" + eventId + " was not found");
    }

    private NotFoundException userNotFound(Long userId) {
        return new NotFoundException("User with id=" + userId + " was not found");
    }
    private NotFoundException commentNotFound(Long userId) {
        return new NotFoundException("Comment with id=" + userId + " was not found");
    }
}
