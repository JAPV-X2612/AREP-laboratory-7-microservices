import React from 'react';

function formatRelative(iso) {
  const date = new Date(iso);
  const diffSec = Math.floor((Date.now() - date.getTime()) / 1000);
  if (diffSec < 60) return `${diffSec}s`;
  if (diffSec < 3600) return `${Math.floor(diffSec / 60)}m`;
  if (diffSec < 86400) return `${Math.floor(diffSec / 3600)}h`;
  if (diffSec < 604800) return `${Math.floor(diffSec / 86400)}d`;
  return date.toLocaleDateString();
}

function PostCard({ post }) {
  const author = post.authorNickname || 'user';
  const initial = author.charAt(0).toUpperCase();

  return (
    <article className="post-card">
      <div className="avatar">{initial}</div>
      <div className="body">
        <div className="meta">
          <span className="author">{author}</span>
          <span className="time">· {formatRelative(post.createdAt)}</span>
        </div>
        <p className="content">{post.content}</p>
      </div>
    </article>
  );
}

export default PostCard;
