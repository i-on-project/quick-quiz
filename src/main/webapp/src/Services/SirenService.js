export const getActionHref = (actions, name) => {
    const action = actions.find(a => a.name === name)
    if (action)
        return action.href
    else
        return null
}

export const getLinksHref = (links, relName, title) => {
    const link = links.find(a => a.rel.includes(relName) && a.title === title)
    if (link)
        return link.href
    else
        return null
}


export const getLinksFromEntity = (entity, relName) => {
    const link = entity.links.find(l => l.rel.includes("self") && l.rel.includes(relName))

    if (link)
        return link.href
    else
        return null
}

export const getEntityLinksHref = (entities, id, relName) => {
    const entity = entities.find(e => e.properties.id === id)
    if (entity) {
        return getLinksFromEntity(entity, relName)
    }
    return null
}

