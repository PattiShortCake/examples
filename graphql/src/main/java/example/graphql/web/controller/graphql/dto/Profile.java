package example.graphql.web.controller.graphql.dto;

import java.util.LinkedList;
import java.util.List;

public class Profile {

  private String id;

  private List<SourceRecord> sourceRecords = new LinkedList<>();

  public Profile(String id, SourceRecord sourceRecord) {
    this.id = id;
    this.sourceRecords.add(sourceRecord);
  }

  public List<SourceRecord> getSourceRecords() {
    return sourceRecords;
  }

  public void addSourceRecords(SourceRecord sourceRecord) {
    this.sourceRecords.add(sourceRecord);
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
