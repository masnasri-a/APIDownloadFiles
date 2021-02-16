package com.index.isa.dependency.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.index.isa.dependency.dao.DAOServices;
import com.index.isa.dependency.model.MessageModel;
import com.index.isa.dependency.service.DependencyService;

import java.io.IOException;

@Service
public class DependencyServiceImplement implements DependencyService {

//	@Autowired
//	DependencyQueryIndex dao;
@Autowired
DAOServices dao;

	@Override
	public MessageModel generateAuthor(String topic, String start, String end) throws IOException {
		MessageModel msg = new MessageModel();
		msg =dao.generateAuthor(topic, start, end);
		return msg;
	}

}
