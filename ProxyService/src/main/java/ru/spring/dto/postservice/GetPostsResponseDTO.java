package ru.spring.dto.postservice;


import java.util.List;

public class GetPostsResponseDTO {
    private List<PostResponseDTO> posts;
    private int totalCount;
    private int page;
    private int pageSize;

    public List<PostResponseDTO> getPosts() { return posts; }
    public void setPosts(List<PostResponseDTO> posts) { this.posts = posts; }
    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
}