export interface LikeActionEntity {
    emoji?: string | null;
    mainId: number;
    total: number;
    type: number;
    users?: {
        nickname?: string | null;
        username?: string | null;
    };
    value?: string | null;
}

export interface LikeEmoji {
    likeValue: string;
    emojiUrl: string;
}