import React from 'react';

/**
 * Displays a single post with its author nickname, content, and creation time.
 *
 * @param {Object} post          the post object to display
 * @param {string} post.id       unique post identifier
 * @param {string} post.content  post body text
 * @param {string} post.authorNickname display name of the author
 * @param {string} post.createdAt ISO-8601 creation timestamp
 */
function PostCard({ post }) {
  return (
    <div style={{ border: '1px solid #ccc', borderRadius: 8, padding: '0.75rem', marginBottom: '0.5rem' }}>
      <strong>{post.authorNickname}</strong>
      <p style={{ margin: '0.4rem 0' }}>{post.content}</p>
      <small style={{ color: '#888' }}>{new Date(post.createdAt).toLocaleString()}</small>
    </div>
  );
}

export default PostCard;
