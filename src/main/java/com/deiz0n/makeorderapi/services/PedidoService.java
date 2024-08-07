package com.deiz0n.makeorderapi.services;

import com.deiz0n.makeorderapi.domain.dtos.PedidoDTO;
import com.deiz0n.makeorderapi.domain.entities.ItensPedido;
import com.deiz0n.makeorderapi.domain.entities.Pedido;
import com.deiz0n.makeorderapi.domain.enums.StatusPedido;
import com.deiz0n.makeorderapi.domain.events.CreatedComandaEvent;
import com.deiz0n.makeorderapi.domain.events.CreatedItensPedidoEvent;
import com.deiz0n.makeorderapi.domain.events.UpdatedItensPedidoEvent;
import com.deiz0n.makeorderapi.domain.exceptions.FuncionarioIsEmptyException;
import com.deiz0n.makeorderapi.domain.exceptions.ItensPedidoIsEmptyException;
import com.deiz0n.makeorderapi.domain.exceptions.MesaIsEmptyException;
import com.deiz0n.makeorderapi.domain.exceptions.PedidoNotFoundException;
import com.deiz0n.makeorderapi.repositories.PedidoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private static final Integer MIN = 1;
    private static final Integer MAX = 1000;

    private PedidoRepository pedidoRepository;
    private ModelMapper mapper;
    private ApplicationEventPublisher publisher;

    public PedidoService(PedidoRepository pedidoRepository, ModelMapper mapper, ApplicationEventPublisher publisher) {
        this.pedidoRepository = pedidoRepository;
        this.mapper = mapper;
        this.publisher = publisher;
    }

    public List<PedidoDTO> getAll() {
        return pedidoRepository.findAll()
                .stream()
                .map(pedidos -> mapper.map(pedidos, PedidoDTO.class))
                .collect(Collectors.toList());
    }

    public PedidoDTO getById(UUID id) {
        return pedidoRepository.findById(id)
                .map(pedido -> mapper.map(pedido, PedidoDTO.class))
                .orElseThrow(() -> new PedidoNotFoundException("Não foi possível encontrar um pedido com o Id informado"));
    }

    public PedidoDTO create(PedidoDTO newPedido) {
        newPedido.setCodigo((int) (Math.random() * MAX) + MIN);
        newPedido.setData(Instant.now());
        newPedido.setStatus(StatusPedido.PENDENTE);

        if (newPedido.getItens().isEmpty()) throw new ItensPedidoIsEmptyException("Não foram adicionados itens aos pedido");
        if (newPedido.getFuncionario() == null) throw new FuncionarioIsEmptyException("Nenhum funcionário foi vinculado ao pedido");
        if (newPedido.getMesa() == null) throw new MesaIsEmptyException("Nenhuma mesa foi vinculada ao pedido");

        var comandaEvent = new CreatedComandaEvent(this, newPedido.getComanda());
        publisher.publishEvent(comandaEvent);

        var pedido = mapper.map(newPedido, Pedido.class);

        pedidoRepository.save(pedido);

        for (ItensPedido item : pedido.getItens()) {
            item.setPedido(pedido);
            var createdEvent = new CreatedItensPedidoEvent(this, item);
            publisher.publishEvent(createdEvent);
        }
        return newPedido;
    }

    public void delete(UUID id) {
        var pedido = getById(id);
        pedidoRepository.deleteById(pedido.getId());
    }

    public PedidoDTO update(UUID id, PedidoDTO newData) {
        try {
            var pedido = pedidoRepository.getReferenceById(id);

            if (newData.getItens().isEmpty()) {
                BeanUtils.copyProperties(newData, pedido, "id", "codigo", "data", "itens", "comanda", "funcionario", "mesa");
                pedidoRepository.save(pedido);
            }
            else {
                BeanUtils.copyProperties(newData, pedido, "id", "codigo", "data", "comanda", "funcionario", "mesa");

                for (ItensPedido itens : newData.getItens()) {
                    pedidoRepository.save(pedido);

                    itens.setPedido(pedido);

                    var updatedEvent = new UpdatedItensPedidoEvent(this, itens);
                    publisher.publishEvent(updatedEvent);
                }
            }

            return mapper.map(pedido, PedidoDTO.class);
        } catch (FatalBeanException e) {
            throw new PedidoNotFoundException("Não foi possível encontrar o pedido com o Id informado");
        }
    }

    public PedidoDTO updateStatus(UUID id, PedidoDTO newStatus) {
        try {
            var pedido = pedidoRepository.getReferenceById(id);

            BeanUtils.copyProperties(newStatus, pedido, "id", "codigo", "data", "itens");
            pedidoRepository.save(pedido);
            return mapper.map(pedido, PedidoDTO.class);
        } catch (FatalBeanException e) {
            throw new PedidoNotFoundException("Não foi possível encontrar o pedido com o Id informado");
        }

    }

}
