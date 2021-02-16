package com.index.isa.dependency.service;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.index.isa.dependency.model.MessageModel;

public interface DependencyService {

	MessageModel generateAuthor(String topic,String start, String end) throws IOException;

}