import React, { useState } from 'react';
import { useAuth0 } from '@auth0/auth0-react';
import { createPost } from '../../services/apiService';

function PostForm({ onPostCreated }) {
  const { getAccessTokenSilently } = useAuth0();
  const [content, setContent] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const remaining = 140 - content.length;
  const counterClass = remaining <= 0 ? 'counter danger' : remaining < 20 ? 'counter warn' : 'counter';

  async function handleSubmit(e) {
    e.preventDefault();
    if (!content.trim()) return;
    setLoading(true);
    setError('');
    try {
      const token = await getAccessTokenSilently({
        authorizationParams: { audience: import.meta.env.VITE_AUTH0_AUDIENCE },
      });
      const post = await createPost(content, token);
      setContent('');
      onPostCreated(post);
    } catch (err) {
      const msg = err?.response?.data?.error || err?.message || 'Failed to create post. Please try again.';
      setError(msg);
    } finally {
      setLoading(false);
    }
  }

  return (
    <form className="post-form" onSubmit={handleSubmit}>
      <textarea
        value={content}
        onChange={(e) => setContent(e.target.value)}
        maxLength={140}
        placeholder="What's happening?"
        rows={3}
      />
      <div className="actions">
        <span className={counterClass}>{remaining}</span>
        <button type="submit" className="primary" disabled={loading || !content.trim()}>
          {loading ? 'Posting...' : 'Post'}
        </button>
      </div>
      {error && <div className="error-banner">{error}</div>}
    </form>
  );
}

export default PostForm;
