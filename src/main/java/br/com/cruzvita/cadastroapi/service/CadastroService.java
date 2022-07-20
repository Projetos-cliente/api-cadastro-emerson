package br.com.cruzvita.cadastroapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.cruzvita.cadastroapi.constants.Status;
import br.com.cruzvita.cadastroapi.dto.CadastroDTO;
import br.com.cruzvita.cadastroapi.entity.Pessoa;
import br.com.cruzvita.cadastroapi.entity.TipoIdentificador;
import br.com.cruzvita.cadastroapi.repository.PessoaRepository;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class CadastroService {
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	public ResponseEntity<String> cadastrarPessoa(CadastroDTO cadastro) {
		try {
			Pessoa pessoa = new Pessoa();
			
			pessoa.setNome(cadastro.getNome());
			pessoa.setIdentificador(cadastro.getIdentificador());
			
			if(pessoa.getIdentificador().chars().filter(ch -> ch != ' ').count() == 11) {
				pessoa.setTipoId(TipoIdentificador.CPF);
			}else if (pessoa.getIdentificador().chars().filter(ch -> ch != ' ').count() == 14) {
				pessoa.setTipoId(TipoIdentificador.CNPJ);
			}
			pessoaRepository.save(pessoa);
			return new ResponseEntity<>(Status.CADASTRO_SUCESSO, HttpStatus.OK);
					
			
			
		} catch (Exception e) {
			log.error(Status.CADASTRO_ERRO);
			return new ResponseEntity<>(Status.CADASTRO_ERRO, HttpStatus.BAD_GATEWAY);
		}
	}

	public ResponseEntity<List<Pessoa>> buscarTodos() {
		try {
			log.info("Buscando todos os cadastros");
			List<Pessoa> cadastros = pessoaRepository.findAll();
			return new ResponseEntity<>(cadastros, HttpStatus.OK);	
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	
	}

	public ResponseEntity<CadastroDTO> consultarPessoa(String identificador) {
		try {
			log.info("inciando busca por pessoas");
			CadastroDTO cadastro = pessoaRepository.findByIdentificador(identificador); 
			if (cadastro.getIdentificador() != null || !cadastro.getIdentificador().isEmpty()) {
				log.info("Aluno encontrado");
				return ResponseEntity.ok(cadastro);
			}
			
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.internalServerError().build();
	}

}
