package edu.eci.arep.dto;

import java.util.List;

/**
 * Outbound DTO wrapping the ordered list of posts for the global stream endpoint.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
public class StreamResponse {

    private List<PostItem> posts;

    public StreamResponse() {}

    /**
     * @param posts ordered list of post items (newest first)
     */
    public StreamResponse(List<PostItem> posts) {
        this.posts = posts;
    }

    public List<PostItem> getPosts() { return posts; }
    public void setPosts(List<PostItem> posts) { this.posts = posts; }

    /**
     * Flat post representation for stream consumption.
     */
    public static class PostItem {

        private String id;
        private String content;
        private String authorNickname;
        private String createdAt;

        public PostItem() {}

        /**
         * @param id             post unique identifier
         * @param content        post body
         * @param authorNickname author display name
         * @param createdAt      ISO-8601 creation timestamp
         */
        public PostItem(String id, String content, String authorNickname, String createdAt) {
            this.id = id;
            this.content = content;
            this.authorNickname = authorNickname;
            this.createdAt = createdAt;
        }

        public String getId() { return id; }
        public String getContent() { return content; }
        public String getAuthorNickname() { return authorNickname; }
        public String getCreatedAt() { return createdAt; }

        public void setId(String id) { this.id = id; }
        public void setContent(String content) { this.content = content; }
        public void setAuthorNickname(String authorNickname) { this.authorNickname = authorNickname; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    }
}
