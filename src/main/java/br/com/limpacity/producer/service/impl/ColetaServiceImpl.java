package br.com.limpacity.producer.service.impl;

import br.com.limpacity.producer.converter.ColetaConverter;
import br.com.limpacity.producer.dto.ColetaDTO;
import br.com.limpacity.producer.exception.ColetaNotFoundException;
import br.com.limpacity.producer.model.ColetaModel;
import br.com.limpacity.producer.repository.ColetaRepository;
import br.com.limpacity.producer.service.ColetaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class ColetaServiceImpl implements ColetaService {

    final ColetaRepository coletaRepository;

    @Override
    public ColetaModel create(ColetaDTO material) {
        ColetaModel mat = coletaRepository.save(toDto(material));
        return mat;
    }

    private ColetaModel toDto(ColetaDTO dto) {
        return ColetaModel.builder()
                .quantidade(dto.getQuantidade())
                .integrationStatus(dto.getIntegrationStatus())
                .creationDate(new Date())
                .build();
    }

    @Override
    public List<ColetaDTO> findAllAndIntegrationStatus() {
        final List<ColetaModel> result = coletaRepository.findAllAndIntegrationStatus();

        if(result.isEmpty()){
            throw new ColetaNotFoundException();
        }
        return ColetaConverter.toColetaList(result);
    }

    @Override
    public ColetaDTO updateColeta(Long id, ColetaDTO material) {
        var opColeta = this.coletaRepository.findById(id)
                .orElseThrow(()-> new ColetaNotFoundException());
        Date creationDate =  opColeta.getCreationDate();
        ColetaModel mat = coletaRepository.save(toUpdate(id, material, creationDate));
        return toColeta(mat);
    }

    private static ColetaDTO toColeta(ColetaModel dto){
        return ColetaDTO.builder()
                .id(dto.getId())
                .quantidade(dto.getQuantidade())
                .integrationStatus(dto.getIntegrationStatus())
                .build();
    }

    private ColetaModel toUpdate(Long id, ColetaDTO dto, Date creationDate) {
        return ColetaModel.builder()
                .id(id)
                .quantidade(dto.getQuantidade())
                .integrationStatus(dto.getIntegrationStatus())
                .creationDate(creationDate)
                .updateDate(new Date())
                .build();
    }

    @Override
    public Object inactiveColeta(Long id) {
        var opMaterial = this.coletaRepository.findById(id)
                .orElseThrow(()-> new ColetaNotFoundException());
        opMaterial.setUpdateDate(new Date());
        opMaterial.setIntegrationStatus("N");
        this.coletaRepository.save(opMaterial);
        return id;
    }

}
